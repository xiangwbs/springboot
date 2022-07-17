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
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
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
public class UserEsService {
    public static final String INDEX = "moo_user";
    private final EsHelper esBaseService;

    public void upsert(UserEsVO dto) {
        UpsertDoc doc = UpsertDoc.of(String.valueOf(dto.getUserId()), dto);
        esBaseService.upsert(doc, INDEX);
    }

    public void bulkUpsert(List<UserEsVO> list) {
        List<UpsertDoc> docs = list.stream().map(dto -> UpsertDoc.of(String.valueOf(dto.getUserId()), dto))
                .collect(Collectors.toList());
        esBaseService.bulkUpsert(docs, INDEX);
    }

    public Integer count(String nickName) {
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        bool.must(QueryBuilders.termQuery("nickName", nickName));
        return esBaseService.count(bool, INDEX);
    }

    public PageVO<UserEsVO> searchUser(UserEsDTO dto) {
        BoolQueryBuilder bool = this.userBool(dto);
        FunctionScoreQueryBuilder functionScore = this.userFunctionScore(bool);
        HighlightBuilder highlight = this.userHighlight();
        SortBuilder[] sorts = { SortBuilders.fieldSort("_score").order(SortOrder.DESC) };
        return esBaseService.search(functionScore, highlight, sorts, 1, 10, null, null, UserEsVO.class, INDEX);
    }

    private HighlightBuilder userHighlight() {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:#1677ff'>");
        highlightBuilder.postTags("</span>");
        highlightBuilder.field(new HighlightBuilder.Field("title").numOfFragments(0));
        highlightBuilder.field(new HighlightBuilder.Field("content").fragmentSize(100).numOfFragments(100));
        // highlightBuilder.requireFieldMatch(false);
        return highlightBuilder;
    }

    private FunctionScoreQueryBuilder userFunctionScore(QueryBuilder query) {
        // FilterFunctionBuilder[] filterFunctionBuilders;
        // HashMap<String, Object> param = Maps.newHashMap();
        // param.put("origin", LocalDateTime.now());
        // param.put("scale", "365d");
        // param.put("offset", "0d");
        // param.put("decay", 0.5);
        // String scriptStr =
        //         "if(doc['publishDate'].size()>0){return decayDateGauss(params.origin, params.scale, params.offset, params.decay, doc['publishDate'].value);}"
        //                 + "else{return 0;}";
        // Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, scriptStr, param);
        // ScriptScoreFunctionBuilder scriptScoreFunction = ScoreFunctionBuilders.scriptFunction(script).setWeight(100);
        // FilterFunctionBuilder filterFunctionBuilder = new FilterFunctionBuilder(scriptScoreFunction);
        // filterFunctionBuilders = new FilterFunctionBuilder[] { filterFunctionBuilder };
        // return QueryBuilders.functionScoreQuery(query, filterFunctionBuilders).scoreMode(ScoreMode.SUM)
        //         .boostMode(CombineFunction.SUM);

        FilterFunctionBuilder[] fs;
        GaussDecayFunctionBuilder publishDate = ScoreFunctionBuilders
                .gaussDecayFunction("publishDate", LocalDateTime.now(), "365d", "0d", 0.5).setWeight(100);
        FilterFunctionBuilder f1 = new FilterFunctionBuilder(publishDate);

        HashMap<String, Object> param = Maps.newHashMap();
        param.put("origin", LocalDateTime.now());
        param.put("scale", "7d");
        param.put("offset", "0d");
        param.put("decay", 0.5);
        String scriptStr =
                "if(doc['recommendDate'].size()>0&&doc['recommendStatus.code'].size()>0&&doc['recommendStatus.code'].value==1)"
                        + "{return decayDateGauss(params.origin, params.scale, params.offset, params.decay, doc['recommendDate'].value);}"
                        + "else{return 0;}";
        Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, scriptStr, param);
        ScriptScoreFunctionBuilder recommendDate = ScoreFunctionBuilders.scriptFunction(script).setWeight(5);
        FilterFunctionBuilder f2 = new FilterFunctionBuilder(recommendDate);
        fs = new FilterFunctionBuilder[] { f1, f2 };
        return QueryBuilders.functionScoreQuery(query, fs).scoreMode(ScoreMode.SUM).boostMode(CombineFunction.SUM);
    }

    private BoolQueryBuilder userBool(UserEsDTO dto) {
        // Full text queries
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(dto.getNickName())) {
            if (dto.isWasMatchSearch()) {
                // 模糊匹配 会分词
                bool.must(QueryBuilders.matchQuery("nickName", dto.getNickName()).operator(Operator.AND));
            } else {
                // 需要精准匹配
                bool.must(QueryBuilders.matchPhraseQuery("nickName", dto.getNickName()).slop(1));
            }
        }
        if (StringUtils.isNotEmpty(dto.getName())) {
            bool.must(QueryBuilders.multiMatchQuery(dto.getName(), "name", "nickName").type(Type.PHRASE).slop(1)
                    .operator(Operator.AND));
        }
        // 与match_phrase查询一致，但是它将查询字符串的最后一个词作为前缀(prefix)使用
        // 使用场景：自动补全的即时搜索
        // if (StringUtils.isNotEmpty(dto.getXxx())) {
        //     bool.must(QueryBuilders.matchPhrasePrefixQuery("xxx", dto.getXxx()).maxExpansions(20));
        // }
        // Term-level queries
        if (dto.getUserId() != null) {
            bool.must(QueryBuilders.termQuery("userId", dto.getUserId()));
        }
        if (dto.getGender() != null) {
            bool.must(QueryBuilders.termQuery("gender.code", dto.getGender().getCode()));
        }
        if (CollectionUtils.isNotEmpty(dto.getActionTagList())) {
            bool.must(QueryBuilders.termsQuery("actionTagList", dto.getActionTagList()));
        }
        if (CollectionUtils.isNotEmpty(dto.getExcludeUserIds())) {
            bool.mustNot(QueryBuilders.termsQuery("userId", dto.getExcludeUserIds()));
        }
        if (CollectionUtils.isNotEmpty(dto.getCrmTagIdList())) {
            dto.getCrmTagIdList().forEach(id -> bool.must(QueryBuilders.termQuery("crmTagIdList", id)));
        }
        // range 一定要有头有尾 不然会出现慢查询
        if (dto.getCreationBeginTime() != null && dto.getCreationEndTime() != null) {
            bool.filter(QueryBuilders.rangeQuery("creationDate")
                    .from(DateUtil.format(dto.getCreationBeginTime(), DatePattern.NORM_DATETIME_PATTERN))
                    .to(DateUtil.format(dto.getCreationEndTime(), DatePattern.NORM_DATETIME_PATTERN)));
        }
        // constantScore
        bool.should(QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("dingCrmStatus.code", 10)).boost(50));
        return bool;
    }
}
