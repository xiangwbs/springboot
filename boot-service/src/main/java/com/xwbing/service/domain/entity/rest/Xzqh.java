package com.xwbing.service.domain.entity.rest;

import com.xwbing.service.domain.entity.BaseEntity;
import lombok.Data;

/**
 * @description 行政区划表
 * @author xiangwb
 * @date 2024/08/15 15:50
 */
@Data
public class Xzqh extends BaseEntity {
    private static final long serialVersionUID = 5093813048487873651L;
    /**
     * 行政区划代码
     */
    private String xzqhDm;
    /**
     * 行政区划名称
     */
    private String xzqhMc;
    /**
     * 上级行政区划代码
     */
    private String sjxzqhDm;
    /**
     * 行政区划层级
     */
    private String xzqhCj;
}