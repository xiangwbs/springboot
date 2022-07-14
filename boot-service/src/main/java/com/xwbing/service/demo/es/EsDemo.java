package com.xwbing.service.demo.es;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.support.replication.ReplicationResponse.ShardInfo;
import org.elasticsearch.action.support.replication.ReplicationResponse.ShardInfo.Failure;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import com.xwbing.service.util.Jackson;
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
public class EsDemo {
    public static final String INDEX = "moo_user";
    private final RestHighLevelClient restHighLevelClient;

    /**
     * @param dto
     * @param upsert 不存在是否新增
     */
    public void update(UserEsDTO dto, boolean upsert) {
        Long id = dto.getUserId();
        UpdateRequest updateRequest = new UpdateRequest(INDEX, String.valueOf(id))
                .doc(Jackson.build().writeValueAsString(dto), XContentType.JSON)
                .setRefreshPolicy(RefreshPolicy.IMMEDIATE).docAsUpsert(true).retryOnConflict(2);
        try {
            log.info("es update id:{} upsert:{} request:{}", id, upsert, updateRequest.toString());
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            log.info("es update id:{} upsert:{} response:{}", id, upsert, updateResponse.toString());
        } catch (Exception e) {
            log.error("es update id:{} upsert:{} error", id, upsert, e);
        }
    }

    /**
     * @param list
     * @param upsert 不存在是否新增
     */
    public void bulkUpdate(List<UserEsDTO> list, boolean upsert) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        BulkRequest bulkRequest = new BulkRequest();
        list.forEach(dto -> {
            UpdateRequest updateRequest = new UpdateRequest(INDEX, String.valueOf(dto.getUserId()))
                    .doc(Jackson.build().writeValueAsString(dto), XContentType.JSON).docAsUpsert(upsert);
            bulkRequest.add(updateRequest);
        });
        bulkRequest.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
        try {
            log.info("es bulkUpdate upsert:{} bulkRequest:{}", upsert, Jackson.build().writeValueAsString(bulkRequest));
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            log.info("es bulkUpdate upsert:{} response:{}", upsert, Jackson.build().writeValueAsString(bulkResponse));
            if (bulkResponse.hasFailures()) {
                log.error("es bulkUpdate upsert:{} hasFailures:{}", upsert, bulkResponse.buildFailureMessage());
            }
        } catch (Exception e) {
            log.error("es bulkUpdate upsert:{} error", upsert, e);
        }
    }

    public void deleteById(Long id, String index) {
        DeleteRequest request = new DeleteRequest(index, String.valueOf(id));
        request.setRefreshPolicy(RefreshPolicy.IMMEDIATE);
        try {
            log.info("es deleteById id:{} request:{}", id, request.toString());
            DeleteResponse deleteResponse = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            log.info("es deleteById id:{} response:{}", id, deleteResponse.toString());
            ShardInfo shardInfo = deleteResponse.getShardInfo();
            if (shardInfo.getFailed() > 0) {
                List<String> failReasons = Arrays.stream(shardInfo.getFailures()).map(Failure::reason)
                        .collect(Collectors.toList());
                log.error("es deleteById id:{} failed reasons:{}", id, failReasons);
            }
        } catch (Exception e) {
            log.error("es deleteById id:{} error", id, e);
        }
    }

    public void deleteByQuery(QueryBuilder queryBuilder, String index) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        request.setQuery(queryBuilder);
        // 刷新索引
        request.setRefresh(true);
        try {
            log.info("es deleteByQuery dsl:{}", queryBuilder.toString());
            BulkByScrollResponse response = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
            log.info("es deleteByQuery response:{}", response.toString());
            List<BulkItemResponse.Failure> bulkFailures = response.getBulkFailures();
            if (bulkFailures.size() > 0) {
                List<String> failReasons = bulkFailures.stream().map(BulkItemResponse.Failure::getMessage)
                        .collect(Collectors.toList());
                log.error("es deleteByQuery failed reasons:{}", failReasons);
            }
        } catch (Exception e) {
            log.error("es deleteByQuery error", e);
        }
    }

    public <T> T getById(Long id, Class<T> clazz, String index) {
        GetRequest request = new GetRequest(index, String.valueOf(id));
        GetResponse response;
        try {
            log.info("es getById id:{} request:{}", id, request.toString());
            response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            log.info("es getById id:{} response:{}", id, Jackson.build().writeValueAsString(response));
        } catch (Exception e) {
            log.error("es getById id:{} error", id, e);
            return null;
        }
        if (!response.isExists()) {
            return null;
        }
        return Jackson.build().readValue(response.getSourceAsString(), clazz);
    }

    public <T> List<T> listByIds(List<Long> ids, Class<T> clazz, String index) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        MultiGetRequest req = new MultiGetRequest();
        ids.forEach(id -> req.add(index, String.valueOf(id)));
        try {
            log.info("es listByIds ids:{}", ids);
            MultiGetResponse responses = restHighLevelClient.mget(req, RequestOptions.DEFAULT);
            List<T> res = new LinkedList<>();
            for (MultiGetItemResponse itemResponse : responses.getResponses()) {
                if (itemResponse.getFailure() != null) {
                    continue;
                }
                GetResponse getResponse = itemResponse.getResponse();
                if (getResponse.isExists()) {
                    res.add(Jackson.build().readValue(getResponse.getSourceAsString(), clazz));
                }
            }
            return res;
        } catch (Exception e) {
            log.error("es listByIds ids:{} error", ids, e);
            return Collections.emptyList();
        }
    }

    public Integer count(BoolQueryBuilder bool, String index) {
        CountRequest countRequest = new CountRequest(index);
        countRequest.query(bool);
        CountResponse countResponse;
        try {
            log.info("es count dsl:{}", bool.toString());
            countResponse = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
            log.info("es count response:{}", countResponse);
        } catch (Exception e) {
            log.info("es count error", e);
            return 0;
        }
        return Math.toIntExact(countResponse.getCount());
    }

    public PageVO<UserEsVO> searchUser(UserEsDTO dto) {
        BoolQueryBuilder bool = this.userBool(dto);
        SortBuilder[] sorts = { SortBuilders.fieldSort("createDate").order(SortOrder.DESC) };
        return this.search(bool, sorts, 1, 10, null, null, UserEsVO.class, INDEX);
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

    /**
     * @param bool 搜索条件
     * @param sorts 排序
     * @param page 页码
     * @param size 页数大小
     * @param includes 包含字段
     * @param excludes 排除字段
     * @param index 索引
     *
     * @return
     */
    private <T> PageVO<T> search(BoolQueryBuilder bool, SortBuilder[] sorts, int page, int size, String[] includes,
            String[] excludes, Class<T> clazz, String index) {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 搜索条件
        searchSourceBuilder.query(bool);
        // 分页设置
        searchSourceBuilder.from(from(page, size));
        searchSourceBuilder.size(size);
        // 解决数据只显示10000+问题
        searchSourceBuilder.trackTotalHits(true);
        // 超时时间
        searchSourceBuilder.timeout(new TimeValue(3, TimeUnit.SECONDS));
        // 字段设置
        searchSourceBuilder.fetchSource(includes, excludes);
        // 排序设置
        if (ObjectUtils.isNotEmpty(sorts)) {
            Arrays.stream(sorts).forEach(searchSourceBuilder::sort);
        }
        log.info("es search dsl:{}", searchSourceBuilder.toString());
        request.source(searchSourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            log.info("es search response:{}", Jackson.build().writeValueAsString(response));
            log.info("es search took {}ms", response.getTook().getMillis());
            SearchHits hits = response.getHits();
            List<T> collect = Arrays.stream(hits.getHits())
                    .map(searchHit -> Jackson.build().readValue(searchHit.getSourceAsString(), clazz))
                    .collect(Collectors.toList());
            return PageVO.<T>builder().total(hits.getTotalHits().value).data(collect).build();
        } catch (IOException e) {
            log.error("es search error", e);
            return PageVO.empty();
        }
    }

    /**
     * 分页计算
     *
     * @param page
     * @param size
     *
     * @return
     */
    private int from(Integer page, Integer size) {
        return page <= 0 ? 0 : (page - 1) * size;
    }
}
