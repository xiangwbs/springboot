package com.xwbing.service.demo.es;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.elasticsearch.index.query.functionscore.GaussDecayFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreFunctionBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.xwbing.service.service.EsHelper;
import com.xwbing.service.service.EsHelper.UpsertDoc;
import com.xwbing.service.util.PageVO;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2022年07月05日 2:24 PM
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleEsService {
    public static final String INDEX = "moo_articles";
    private final EsHelper esHelper;

    public void upsert(ArticleEsVO dto) {
        UpsertDoc doc = UpsertDoc.of(String.valueOf(dto.getId()), dto);
        esHelper.upsert(doc, INDEX);
    }

    public void bulkUpsert(List<ArticleEsVO> list) {
        List<UpsertDoc> docs = list.stream().map(dto -> UpsertDoc.of(String.valueOf(dto.getId()), dto))
                .collect(Collectors.toList());
        esHelper.bulkUpsert(docs, INDEX);
    }

    public Integer count(String issueDeptCode) {
        TermQueryBuilder query = QueryBuilders.termQuery("issueDeptCode", issueDeptCode);
        return esHelper.count(query, INDEX);
    }

    public PageVO<ArticleEsVO> search(ArticleEsDTO dto) {
        BoolQueryBuilder bool = this.bool(dto);
        FunctionScoreQueryBuilder functionScore = this.functionScore(bool);
        HighlightBuilder highlight = this.highlight();
        SortBuilder[] sorts = { SortBuilders.fieldSort("_score").order(SortOrder.DESC) };
        return esHelper.search(functionScore, highlight, sorts, 1, 10, null, null, ArticleEsVO.class, INDEX);
    }

    private HighlightBuilder highlight() {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        highlightBuilder.field(new HighlightBuilder.Field("title").numOfFragments(0));
        highlightBuilder.field(new HighlightBuilder.Field("content").fragmentSize(100).numOfFragments(50));
        return highlightBuilder;
    }

    private FunctionScoreQueryBuilder functionScore(QueryBuilder query) {
        String now = DateUtil.format(LocalDateTime.now(), DatePattern.NORM_DATETIME_PATTERN);
        GaussDecayFunctionBuilder publishDate = ScoreFunctionBuilders
                .gaussDecayFunction("publishDate", now, "365d", "0d", 0.5).setWeight(100);
        FilterFunctionBuilder f1 = new FilterFunctionBuilder(publishDate);

        HashMap<String, Object> params = Maps.newHashMap();
        params.put("origin", now);
        params.put("scale", "7d");
        params.put("offset", "0d");
        params.put("decay", 0.5);
        String code =
                "if(doc['recommendDate'].size()>0&&doc['recommendStatus.code'].size()>0&&doc['recommendStatus.code'].value==1)"
                        + "{return decayDateGauss(params.origin, params.scale, params.offset, params.decay, doc['recommendDate'].value);}"
                        + "else{return 0;}";
        Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, code, params);
        ScriptScoreFunctionBuilder recommendDate = ScoreFunctionBuilders.scriptFunction(script).setWeight(5);
        FilterFunctionBuilder f2 = new FilterFunctionBuilder(recommendDate);

        return QueryBuilders.functionScoreQuery(query, new FilterFunctionBuilder[] { f1, f2 }).scoreMode(ScoreMode.SUM)
                .boostMode(CombineFunction.SUM);
    }

    private BoolQueryBuilder bool(ArticleEsDTO dto) {
        BoolQueryBuilder bool = QueryBuilders.boolQuery();

        // Full text queries
        String searchKey = dto.getSearchKey();
        if (StringUtils.isNotEmpty(searchKey)) {
            BoolQueryBuilder keyBuilder = QueryBuilders.boolQuery();
            if (dto.isWasMatchSearch()) {
                // 模糊匹配 分词 分为多个term
                //（where term=term0 or term=term1）/（where term=term0 and term=term1）
                keyBuilder.should(QueryBuilders.matchQuery("title", searchKey).operator(Operator.AND)).boost(100);
                keyBuilder.should(QueryBuilders.matchQuery("content", searchKey).operator(Operator.AND)).boost(5);
            } else {
                // 精准匹配 分词 term都包含且顺序一致 slop:term之间的position容错差值
                //（where term=term0 and term0_position=0 and term=term1 and term1_position=1）
                keyBuilder.should(QueryBuilders.matchPhraseQuery("title", searchKey).slop(1)).boost(100);
                keyBuilder.should(QueryBuilders.matchPhraseQuery("content", searchKey).slop(1)).boost(5);
            }
            bool.must(keyBuilder);
        }
        if (StringUtils.isNotEmpty(dto.getIssueDept())) {
            bool.must(QueryBuilders.matchPhraseQuery("issueDept", dto.getIssueDept()).slop(1));
        }
        // 与match_phrase查询一致，但是它将查询字符串的最后一个term作为前缀(prefix)使用
        //（where term=term0 and term0_position=0 and term=term1 and term1_position=1 and term like t%）
        // 使用场景：自动补全的即时搜索
        // if (StringUtils.isNotEmpty(dto.getXxx())) {
        //     bool.must(QueryBuilders.matchPhrasePrefixQuery("xxx", dto.getXxx()).maxExpansions(20));
        // }
        // if (StringUtils.isNotEmpty(dto.getXxx())) {
        //     bool.must(QueryBuilders.multiMatchQuery(dto.getXxx(), "field1", "field2").type(Type.PHRASE).slop(1)
        //             .operator(Operator.AND));
        // }

        // Term-level queries
        bool.must(QueryBuilders.existsQuery("coverUrl"));
        if (CollectionUtils.isNotEmpty(dto.getExcludeIds())) {
            String[] ids = dto.getExcludeIds().stream().map(String::valueOf).toArray(String[]::new);
            bool.mustNot(QueryBuilders.idsQuery().addIds(ids));
        }
        // range 一定要有头有尾 不然会出现慢查询
        if (dto.getPublishDateStart() != null && dto.getPublishDateEnd() != null) {
            bool.filter(QueryBuilders.rangeQuery("publishDate")
                    .from(DateUtil.format(dto.getPublishDateStart(), DatePattern.NORM_DATETIME_PATTERN))
                    .to(DateUtil.format(dto.getPublishDateEnd(), DatePattern.NORM_DATETIME_PATTERN)));
        }
        if (StringUtils.isNotEmpty(dto.getIssueDeptCode())) {
            bool.must(QueryBuilders.termQuery("issueDeptCode", dto.getIssueDeptCode()));
        }
        if (dto.getOperationStatus() != null) {
            bool.must(QueryBuilders.termQuery("operationStatus.code", dto.getOperationStatus().getCode()));
        }
        if (CollectionUtils.isNotEmpty(dto.getCrmTagIdList())) {
            if (dto.isCrmTagOr()) {
                bool.must(QueryBuilders.termsQuery("crmTagIdList", dto.getCrmTagIdList()));
            } else {
                dto.getCrmTagIdList().forEach(id -> bool.must(QueryBuilders.termQuery("crmTagIdList", id)));
            }
        }
        // constantScore 推荐加分
        bool.should(QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("recommendStatus.code", 1)).boost(50));
        return bool;
    }
}