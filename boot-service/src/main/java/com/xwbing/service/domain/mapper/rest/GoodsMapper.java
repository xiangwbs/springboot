package com.xwbing.service.domain.mapper.rest;

import org.apache.ibatis.annotations.Param;

import com.xwbing.service.domain.entity.rest.Goods;
import com.xwbing.service.domain.mapper.BaseMapper;

/**
 * @author xiangwb
 */
public interface GoodsMapper extends BaseMapper<Goods> {
    int decreaseStock(@Param("id") String id, @Param("num") Integer num);

    int increaseStock(@Param("id") String id, @Param("num") Integer num);
}