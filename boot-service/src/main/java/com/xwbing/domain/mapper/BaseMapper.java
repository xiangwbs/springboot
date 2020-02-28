package com.xwbing.domain.mapper;

import java.util.List;
import java.util.Map;

/**
 * @author: xiangwb
 * @date: 2018/06/04 20:14
 * @description: BaseMapper
 */
public interface BaseMapper<M> {
    int insert(M model);

    int insertBatch(List<M> models);

    int deleteById(String id);

    int deleteByIds(List<String> ids);

    int delete(Map<String, Object> conditions);

    int update(M model);

    int updateBatch(List<M> models);

    M findById(String id);

    List<M> findByIds(List<String> ids);

    List<M> find(Map<String, Object> conditions);

    List<M> findAll();
}

