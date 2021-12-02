package com.xwbing.service.domain.entity.model;

import lombok.Data;

/**
 * 创建时间: 2017/11/17 9:22
 * 作者: xiangwb
 * 说明: 快递追踪信息
 */
@Data
public class ExpressTrace {
    /**
     * 时间
     */
    private String AcceptTime;
    /**
     * 描述
     */
    private String AcceptStation;
    /**
     * 备注
     */
    private String Remark;
}
