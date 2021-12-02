package com.xwbing.service.domain.entity.vo;

import java.util.List;

import com.xwbing.service.domain.entity.model.ExpressTrace;

import lombok.Data;

/**
 * 创建时间: 2017/11/17 9:20
 * 作者: xiangwb
 * 说明: 快递信息展示
 */
@Data
public class ExpressInfoVo {
    /**
     * 快递公司编码
     */
    private String ShipperCode;
    /**
     * 物流运单号
     */
    private String LogisticCode;
    private boolean Success;
    /**
     * 失败原因
     */
    private String Reason;
    /**
     * 状态
     */
    private String State;
    /**
     * 物流信息
     */
    private List<ExpressTrace> Traces;
    /**
     * 描述
     */
    private String Describe;
}
