package com.xwbing.service.demo.es.user;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.enums.base.BaseEnum;
import com.xwbing.service.exception.BusinessException;
import com.xwbing.service.util.Jackson;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2022年08月19日 10:46 AM
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class UserEsService {

    /**
     * 组装高级筛选条件
     * 所有查询都基于termQuery，可在EsField扩展自定义查询类型
     *
     * @param conditions 条件列表 包含relationType和条件，只会存在一条，日期筛选可以存在2条，超过2条是不合法条件
     * @param searchRelationType 条件关系 MUST|SHOULD
     *
     * @return
     */
    public BoolQueryBuilder buildBoolQuery(List<UserParamVO> conditions, SearchRelationTypeEnum searchRelationType) {
        if (CollectionUtils.isEmpty(conditions)) {
            log.error("getBoolQuery conditions null");
            return null;
        }
        log.info("getBoolQuery conditions:{}", conditions);
        try {
            BoolQueryBuilder bool = QueryBuilders.boolQuery();
            for (UserParamVO condition : conditions) {
                log.info("getBoolQuery condition:{}", condition);
                // 1.获取关系
                SearchRelationTypeEnum relationType = condition.getRelationType();
                // 2.获取有值字段
                List<Field> fields = Arrays.stream(condition.getClass().getDeclaredFields()).filter(field -> {
                    try {
                        field.setAccessible(true);
                        return field.getAnnotation(EsField.class) != null && ObjectUtils
                                .isNotEmpty(field.get(condition)) && !field.getType()
                                .equals(SearchRelationTypeEnum.class);
                    } catch (Exception e) {
                        return false;
                    }
                }).collect(Collectors.toList());
                log.info("getBoolQuery fields:{}", fields.stream().map(Field::getName).collect(Collectors.toList()));
                // 3.组装条件
                BoolQueryBuilder conditionBool = QueryBuilders.boolQuery();
                if (fields.size() == 1) {
                    Field field = fields.get(0);
                    field.setAccessible(true);
                    Object value = field.get(condition);
                    EsField esField = field.getAnnotation(EsField.class);
                    String name = esField.value();
                    boolean isList = field.getType().equals(List.class);
                    if (isList && name.contains(".code")) {
                        value = ((List<BaseEnum>)value).stream().map(BaseEnum::getCode).collect(Collectors.toList());
                    }
                    if (value instanceof BaseEnum) {
                        value = ((BaseEnum)value).getCode();
                    }
                    // 是|在|包含
                    if (SearchRelationTypeEnum.MUST.equals(relationType)) {
                        if (isList) {
                            conditionBool.must(QueryBuilders.termsQuery(name, (List)value));
                        } else {
                            conditionBool.must(QueryBuilders.termQuery(name, value));
                        }
                    }
                    // 不是|不在|不包含
                    else if (SearchRelationTypeEnum.MUST_NOT.equals(relationType)) {
                        if (isList) {
                            conditionBool.mustNot(QueryBuilders.termsQuery(name, (List)value));
                        } else {
                            conditionBool.mustNot(QueryBuilders.termQuery(name, value));
                        }
                    }
                    // 早于
                    else if (SearchRelationTypeEnum.BEFORE.equals(relationType)) {
                        conditionBool.filter(QueryBuilders.rangeQuery(name).lt(DateUtil
                                .format(((LocalDate)value).plusDays(1).atStartOfDay(),
                                        DatePattern.NORM_DATETIME_PATTERN)));
                    }
                    // 晚于
                    else if (SearchRelationTypeEnum.AFTER.equals(relationType)) {
                        conditionBool.filter(QueryBuilders.rangeQuery(name).gt(DateUtil
                                .format(((LocalDate)value).atStartOfDay(), DatePattern.NORM_DATETIME_PATTERN)));
                    } else {
                        log.error("getBoolQuery condition:{} invalid relationType:{}", condition, relationType);
                        throw new BusinessException("高级筛选条件错误");
                    }
                }
                // 2个字段表明是时间范围
                else if (fields.size() == 2) {
                    LocalDateTime beginTime = ((LocalDate)fields.get(0).get(condition)).atStartOfDay();
                    LocalDateTime endTime = ((LocalDate)fields.get(1).get(condition)).plusDays(1).atStartOfDay();
                    EsField esField = fields.get(0).getAnnotation(EsField.class);
                    String name = esField.value();
                    RangeQueryBuilder range = QueryBuilders.rangeQuery(name)
                            .from(DateUtil.format(beginTime, DatePattern.NORM_DATETIME_PATTERN))
                            .to(DateUtil.format(endTime, DatePattern.NORM_DATETIME_PATTERN));
                    // 在
                    if (SearchRelationTypeEnum.MUST.equals(relationType)) {
                        conditionBool.filter(range);
                    }
                    // 不在
                    else if (SearchRelationTypeEnum.MUST_NOT.equals(relationType)) {
                        conditionBool.mustNot(range);
                    } else {
                        log.error("getBoolQuery  condition:{} invalid relationType:{}", condition, relationType);
                        throw new BusinessException("高级筛选条件错误");
                    }
                } else {
                    log.error("getBoolQuery condition:{} fields>2", condition);
                    throw new BusinessException("高级筛选条件错误");
                }
                // 4.组装条件关系
                if (SearchRelationTypeEnum.MUST.equals(searchRelationType)) {
                    bool.must(conditionBool);
                } else if (SearchRelationTypeEnum.SHOULD.equals(searchRelationType)) {
                    bool.should(conditionBool);
                } else {
                    log.error("getBoolQuery invalid relationType:{}", searchRelationType);
                    throw new BusinessException("高级筛选关系错误");
                }
            }
            return bool;
        } catch (Exception e) {
            log.error("getBoolQuery error", e);
            throw new BusinessException("高级筛选条件异常");
        }
    }

    /**
     * 基础删选参数转化为高级筛选条件
     * searchRelationType默认SearchRelationTypeEnum.MUST
     *
     * @param param
     *
     * @return
     */
    private List<String> buildCondition(UserParamVO param) {
        Map<String, JSONObject> conditionMap = new HashMap<>();
        for (Field field : param.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = null;
            try {
                value = field.get(param);
            } catch (Exception e) {
                log.error("buildNormalCondition field:{} error", name, e);
            }
            if (ObjectUtils.isEmpty(value)) {
                continue;
            }
            EsField esField = field.getAnnotation(EsField.class);
            if (esField == null) {
                continue;
            }
            String esName = esField.value();
            JSONObject conditionObject = conditionMap.get(esName);
            if (conditionObject == null) {
                conditionObject = new JSONObject();
                conditionObject.put("relationType", SearchRelationTypeEnum.MUST);
                conditionObject.put(name, value);
                conditionMap.put(esName, conditionObject);
            } else {
                conditionObject.put(name, value);
                conditionMap.put(esName, conditionObject);
            }
        }
        return conditionMap.values().stream()
                .map(conditionObject -> Jackson.build().writeValueAsString(conditionObject))
                .collect(Collectors.toList());
    }
}