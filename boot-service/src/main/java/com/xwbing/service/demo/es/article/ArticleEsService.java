package com.xwbing.service.demo.es.article;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xwbing.service.service.EsHelper;
import com.xwbing.service.service.EsHelper.UpsertDoc;
import com.xwbing.service.util.PageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder.FilterFunctionBuilder;
import org.elasticsearch.index.query.functionscore.GaussDecayFunctionBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreFunctionBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Sum;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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

    // public long update(Long id, String title, String content) {
    //     UpdateByQueryRequest request = new UpdateByQueryRequest(INDEX);
    //     request.setRefresh(true);
    //     request.setBatchSize(500);
    //     request.setQuery(QueryBuilders.termQuery("id", id));
    //     Map<String, Object> params = Maps.newHashMap();
    //     params.put("title", title);
    //     params.put("content", content);
    //     Script script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG,
    //             "ctx._source.title=params.title;ctx._source.content=content",
    //             params);
    //     request.setScript(script);
    //     try {
    //         log.info("update script:{}", script.toString());
    //         BulkByScrollResponse response = restHighLevelClient.updateByQuery(request, RequestOptions.DEFAULT);
    //         log.info("update res:{}", response.toString());
    //         return response.getTotal();
    //     } catch (IOException e) {
    //         log.error("update error", e);
    //         return 0;
    //     }
    // }

    public Integer count(String issueDeptCode) {
        TermQueryBuilder query = QueryBuilders.termQuery("issueDeptCode", issueDeptCode);
        return esHelper.count(query, INDEX);
    }

    public PageVO<ArticleEsVO> search(ArticleEsDTO dto) {
        BoolQueryBuilder bool = this.bool(dto);
        FunctionScoreQueryBuilder functionScore = this.functionScore(bool);
        HighlightBuilder highlight = this.highlight();
//        SortBuilder[] sorts = { SortBuilders.fieldSort("_score").order(SortOrder.DESC) };
        SortBuilder[] sorts = {SortBuilders.scoreSort()};
        return esHelper.search(functionScore, highlight, sorts, 1, 10, null, null, ArticleEsVO.class, INDEX);
    }

    public void agg(ArticleEsDTO dto) {
        BoolQueryBuilder bool = this.bool(dto);
        TermsAggregationBuilder issueTermsAgg = AggregationBuilders.
                terms("issueDeptGroup")
                .field("issueDept")
                .size(1000)
                .subAggregation(AggregationBuilders.sum("operationStatus.code").field("operationStatusSum"));
        Aggregations aggregations = esHelper.agg(INDEX, bool, Collections.singletonList(issueTermsAgg));
        Terms issueTerms = aggregations.get("issueDeptGroup");
        issueTerms.getBuckets().stream()
                .map(bucket -> {
                    String issueDept = bucket.getKeyAsString();
                    long count = bucket.getDocCount();
                    Sum operationStatusSum = bucket.getAggregations().get("operationStatusSum");
                    long sum = new Double(operationStatusSum.getValue()).longValue();
                    return null;
                }).collect(Collectors.toList());
    }

    private HighlightBuilder highlight() {
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
//        Arrays.asList("title", "content").forEach(highlightBuilder::field);
        // 从第一个分片获取高亮片段
        highlightBuilder.field(new HighlightBuilder.Field("title").numOfFragments(0));
        // fragmentSize高亮片段最大字符长度 numOfFragments最大高亮片段数
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

        return QueryBuilders.functionScoreQuery(query, new FilterFunctionBuilder[]{f1, f2}).scoreMode(ScoreMode.SUM).boostMode(CombineFunction.SUM);
    }

    private BoolQueryBuilder bool(ArticleEsDTO dto) {
        BoolQueryBuilder bool = QueryBuilders.boolQuery();

        // Full text queries
        String searchKey = dto.getSearchKey();
        if (StringUtils.isNotEmpty(searchKey)) {
            BoolQueryBuilder keyBuilder = QueryBuilders.boolQuery();
            if (dto.isWasMatchSearch()) {
                // 模糊匹配 分词 分为多个term
                //（where token=term0 or token=term1）默认/（where token=term0 and token=term1）
//                keyBuilder.must(QueryBuilders.multiMatchQuery(searchKey, "title", "content").analyzer("ik_smart"));
//                Map<String, Float> fields = Maps.newHashMap();
//                fields.put("title", 10f);
//                fields.put("content", 1f);
//                keyBuilder.must(QueryBuilders.multiMatchQuery(searchKey).fields(fields).analyzer("ik_smart"));
                keyBuilder.should(QueryBuilders.matchQuery("title", searchKey).operator(Operator.AND)).boost(10);
                keyBuilder.should(QueryBuilders.matchQuery("content", searchKey).operator(Operator.AND)).boost(1);
            } else {
                // 精准匹配 分词 term都包含且顺序一致 slop:term之间的position容错差值
                //（where token=term0 and token=term1 and term1_position-term0_position<=1）
                keyBuilder.should(QueryBuilders.matchPhraseQuery("title", searchKey).analyzer("ik_smart").slop(1)).boost(10);
                keyBuilder.should(QueryBuilders.matchPhraseQuery("content", searchKey).analyzer("ik_smart").slop(1)).boost(1);
            }
            bool.must(keyBuilder);
        }
        if (StringUtils.isNotEmpty(dto.getIssueDept())) {
            bool.must(QueryBuilders.matchPhraseQuery("issueDept", dto.getIssueDept()).slop(1));
        }
        // 与match_phrase查询一致，但是它将查询字符串的最后一个token作为前缀(prefix)使用
        //（where token=term0 and token=term1 and term1_position-term0_position+term2_position-term1_position=0 and token like term2%）
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
        // 查询本级及下级
        if (StringUtils.isNotEmpty(dto.getSwjgDm())) {
            bool.must(QueryBuilders.prefixQuery("swjgDm", dto.getSwjgDm().replaceAll("0+$", "")));
        }
        // 如果查询本级及以上，需要组装本级以上swjgdmPath
        if (StringUtils.isNotEmpty(dto.getSwjgDmPath())) {
            List<String> paths = buildParentPath(dto.getSwjgDmPath());
            paths.add(dto.getSwjgDmPath());
            bool.must(QueryBuilders.termsQuery("swjgDmPath", paths));
        }
        // nested 查询本级以及以上
        BoolQueryBuilder regionBool = regionBool("13300000000", "13301000000", "13301100000");
        bool.must(QueryBuilders.nestedQuery("regionList", regionBool, org.apache.lucene.search.join.ScoreMode.None));
        // constantScore 推荐加分
        bool.should(QueryBuilders.constantScoreQuery(QueryBuilders.termQuery("recommendStatus.code", 1)).boost(50));
        return bool;
    }

    private BoolQueryBuilder regionBool(String provinceCode, String cityCode, String districtCode) {
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        //全域
        bool.should(QueryBuilders.termQuery("regionList.provinceCode", "-1"));
        //省域
        if (regionCheck(provinceCode)) {
            TermQueryBuilder provinceQueryBuilder = QueryBuilders.termQuery("regionList.provinceCode", provinceCode);
            bool.should(QueryBuilders.boolQuery().must(provinceQueryBuilder).must(QueryBuilders.termQuery("regionList.cityCode", "-1")));
            //市域
            if (regionCheck(cityCode)) {
                TermQueryBuilder cityQueryBuilder = QueryBuilders.termQuery("regionList.cityCode", cityCode);
                bool.should(QueryBuilders.boolQuery().must(provinceQueryBuilder).must(cityQueryBuilder).must(QueryBuilders.termQuery("regionList.districtCode", "-1")));
                //区域
                if (regionCheck(districtCode)) {
                    bool.should(QueryBuilders.boolQuery().must(provinceQueryBuilder).must(cityQueryBuilder).must(QueryBuilders.termQuery("regionList.districtCode", districtCode)));
                }
            }
        }
        return bool;
    }

    private static Boolean regionCheck(String code) {
        return StringUtils.isNotBlank(code) && !"-1".equals(code);
    }

    private static List<String> buildParentPath(String path) {
        List<String> parentPaths = Lists.newArrayList();
        String[] swjgdms = path.split("-");
        for (int i = swjgdms.length - 1; i > 0; i--) {
            StringBuilder parentPath = new StringBuilder();
            for (int j = 0; j < i; j++) {
                parentPath.append(swjgdms[j]);
                if (j < i - 1) {
                    parentPath.append("-");
                }
            }
            parentPaths.add(parentPath.toString());
        }
        return parentPaths;
    }

    public static void main(String[] args) {
        List<String> parentPathList = buildParentPath("00000000000-13300000000-13301000000-13301100000");
        System.out.println("");
    }
}