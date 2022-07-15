package com.xwbing.service.demo.es;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import com.xwbing.service.service.BaseEsService;
import com.xwbing.service.service.BaseEsService.UpsertDoc;
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
    private final BaseEsService baseEsService;

    /**
     * 单个更新或者新增
     *
     * @param dto
     */
    public void upsert(UserEsVO dto) {
        UpsertDoc doc = UpsertDoc.of(String.valueOf(dto.getUserId()), dto);
        baseEsService.upsert(doc, INDEX);
    }

    /**
     * 批量新增或者更新
     *
     * @param list
     */
    public void bulkUpsert(List<UserEsVO> list) {
        List<UpsertDoc> docs = list.stream().map(dto -> UpsertDoc.of(String.valueOf(dto.getUserId()), dto))
                .collect(Collectors.toList());
        baseEsService.bulkUpsert(docs, INDEX);
    }

    public Integer count(String nickName) {
        BoolQueryBuilder bool = QueryBuilders.boolQuery();
        bool.must(QueryBuilders.termQuery("nickName", nickName));
        return baseEsService.count(bool, INDEX);
    }

    public PageVO<UserEsVO> searchUser(UserEsDTO dto) {
        BoolQueryBuilder bool = this.userBool(dto);
        SortBuilder[] sorts = { SortBuilders.fieldSort("creationDate").order(SortOrder.DESC) };
        return baseEsService.search(bool, sorts, 1, 10, null, null, UserEsVO.class, INDEX);
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
