package com.xwbing.service.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
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
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.stereotype.Service;

import com.xwbing.service.util.Jackson;
import com.xwbing.service.util.PageVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2022年07月15日 9:21 AM
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class BaseEsService {
    private final RestHighLevelClient restHighLevelClient;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpsertDoc {
        private Object doc;
        private String id;

        public static UpsertDoc of(String id, Object doc) {
            return UpsertDoc.builder().id(id).doc(doc).build();
        }
    }

    public void upsert(UpsertDoc doc, String index) {
        String id = doc.getId();
        UpdateRequest request = new UpdateRequest(index, id)
                .doc(Jackson.build().writeValueAsString(doc.getDoc()), XContentType.JSON)
                .setRefreshPolicy(RefreshPolicy.IMMEDIATE).docAsUpsert(true).retryOnConflict(2);
        try {
            log.info("elasticsearch upsert id:{} request:{}", id, request.toString());
            UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
            log.info("elasticsearch upsert id:{} response:{}", id, response.toString());
            ShardInfo shardInfo = response.getShardInfo();
            if (shardInfo.getFailed() > 0) {
                List<String> failReasons = Arrays.stream(shardInfo.getFailures()).map(Failure::reason)
                        .collect(Collectors.toList());
                log.error("elasticsearch upsert id:{} failed reasons:{}", id, failReasons);
            }
        } catch (Exception e) {
            log.error("elasticsearch upsert id:{} error", id, e);
        }
    }

    public void bulkUpsert(List<UpsertDoc> docs, String index) {
        if (CollectionUtils.isEmpty(docs)) {
            return;
        }
        BulkRequest bulkRequest = new BulkRequest().setRefreshPolicy(RefreshPolicy.IMMEDIATE);
        docs.forEach(doc -> {
            UpdateRequest updateRequest = new UpdateRequest(index, doc.getId())
                    .doc(Jackson.build().writeValueAsString(doc.getDoc()), XContentType.JSON).docAsUpsert(true);
            bulkRequest.add(updateRequest);
        });
        try {
            log.info("elasticsearch bulkUpsert requests:{}", bulkRequest.requests());
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            log.info("elasticsearch bulkUpsert response:{}", Jackson.build().writeValueAsString(bulkResponse));
            if (bulkResponse.hasFailures()) {
                log.error("elasticsearch bulkUpsert hasFailures:{}", bulkResponse.buildFailureMessage());
            }
        } catch (Exception e) {
            log.error("elasticsearch bulkUpsert error", e);
        }
    }

    public void delete(Long id, String index) {
        DeleteRequest request = new DeleteRequest(index, String.valueOf(id)).setRefreshPolicy(RefreshPolicy.IMMEDIATE);
        try {
            log.info("elasticsearch delete id:{} request:{}", id, request.toString());
            DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            log.info("elasticsearch delete id:{} response:{}", id, response.toString());
            ShardInfo shardInfo = response.getShardInfo();
            if (shardInfo.getFailed() > 0) {
                List<String> failReasons = Arrays.stream(shardInfo.getFailures()).map(Failure::reason)
                        .collect(Collectors.toList());
                log.error("elasticsearch delete id:{} failed reasons:{}", id, failReasons);
            }
        } catch (Exception e) {
            log.error("elasticsearch delete id:{} error", id, e);
        }
    }

    public void bulkDelete(List<Long> ids, String index) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        BulkRequest bulkRequest = new BulkRequest().setRefreshPolicy(RefreshPolicy.IMMEDIATE);
        ids.forEach(id -> bulkRequest.add(new DeleteRequest().index(index).id(String.valueOf(id))));
        try {
            log.info("elasticsearch bulkDelete requests:{}", bulkRequest.requests());
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            log.info("elasticsearch bulkDelete response:{}", Jackson.build().writeValueAsString(bulkResponse));
            if (bulkResponse.hasFailures()) {
                log.error("es bulkDelete hasFailures:{}", bulkResponse.buildFailureMessage());
            }
        } catch (Exception e) {
            log.error("elasticsearch bulkDelete error", e);
        }
    }

    public void deleteByQuery(BoolQueryBuilder bool, String index) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index).setRefresh(true).setQuery(bool);
        try {
            log.info("elasticsearch deleteByQuery dsl:{}", bool.toString());
            BulkByScrollResponse response = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
            log.info("elasticsearch deleteByQuery response:{}", response.toString());
            List<BulkItemResponse.Failure> bulkFailures = response.getBulkFailures();
            if (bulkFailures.size() > 0) {
                List<String> failReasons = bulkFailures.stream().map(BulkItemResponse.Failure::getMessage)
                        .collect(Collectors.toList());
                log.error("elasticsearch deleteByQuery failed reasons:{}", failReasons);
            }
        } catch (Exception e) {
            log.error("elasticsearch deleteByQuery error", e);
        }
    }

    public <T> T get(Long id, Class<T> clazz, String index) {
        GetRequest request = new GetRequest(index, String.valueOf(id));
        GetResponse response;
        try {
            log.info("elasticsearch get id:{} request:{}", id, request.toString());
            response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            log.info("elasticsearch get id:{} response:{}", id, response.toString());
        } catch (Exception e) {
            log.error("elasticsearch get id:{} error", id, e);
            return null;
        }
        if (!response.isExists()) {
            return null;
        }
        return Jackson.build().readValue(response.getSourceAsString(), clazz);
    }

    public <T> List<T> mget(List<Long> ids, Class<T> clazz, String index) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        MultiGetRequest request = new MultiGetRequest();
        ids.forEach(id -> request.add(index, String.valueOf(id)));
        try {
            log.info("elasticsearch mget ids:{}", ids);
            MultiGetResponse response = restHighLevelClient.mget(request, RequestOptions.DEFAULT);
            log.info("elasticsearch mget ids:{} response:{}", ids, Jackson.build().writeValueAsString(response));
            List<T> res = new LinkedList<>();
            for (MultiGetItemResponse itemResponse : response.getResponses()) {
                if (itemResponse.getFailure() != null) {
                    log.error("elasticsearch mget error:{}", itemResponse.getFailure().getMessage());
                    continue;
                }
                GetResponse getResponse = itemResponse.getResponse();
                if (getResponse.isExists()) {
                    res.add(Jackson.build().readValue(getResponse.getSourceAsString(), clazz));
                }
            }
            return res;
        } catch (Exception e) {
            log.error("elasticsearch mget ids:{} error", ids, e);
            return Collections.emptyList();
        }
    }

    public Integer count(BoolQueryBuilder bool, String index) {
        CountRequest request = new CountRequest(index).query(bool);
        CountResponse countResponse;
        try {
            log.info("elasticsearch count dsl:{}", bool.toString());
            countResponse = restHighLevelClient.count(request, RequestOptions.DEFAULT);
            log.info("elasticsearch count response:{}", countResponse);
        } catch (Exception e) {
            log.info("elasticsearch count error", e);
            return 0;
        }
        return Math.toIntExact(countResponse.getCount());
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
    public <T> PageVO<T> search(BoolQueryBuilder bool, SortBuilder[] sorts, int page, int size, String[] includes,
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
        log.info("elasticsearch search source:{}", searchSourceBuilder.toString());
        request.source(searchSourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            log.info("elasticsearch search response:{}", response.toString());
            log.info("elasticsearch search took {}ms", response.getTook().getMillis());
            SearchHits hits = response.getHits();
            List<T> collect = Arrays.stream(hits.getHits())
                    .map(searchHit -> Jackson.build().readValue(searchHit.getSourceAsString(), clazz))
                    .collect(Collectors.toList());
            return PageVO.<T>builder().total(hits.getTotalHits().value).data(collect).build();
        } catch (Exception e) {
            log.error("elasticsearch search error", e);
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
