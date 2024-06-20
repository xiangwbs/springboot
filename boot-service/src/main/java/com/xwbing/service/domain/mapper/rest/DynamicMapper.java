/**
 * Copyright(C) 2023 Hangzhou Fugle Technology Co., Ltd. All rights reserved.
 */
package com.xwbing.service.domain.mapper.rest;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author muyu
 * @version $Id$
 * @since 2023-10-12 18:13:08
 */
public interface DynamicMapper {
    /**
     * 基于sql动态查询数据
     * LinkedHashMap可以保持返回数据顺序和sql语句中字段顺序一致
     *
     * @param sql
     * @return
     */
    List<Map<String, Object>> selectBySql(@Param("sql") String sql);

    void insertBySql(@Param("sql") String sql);
}