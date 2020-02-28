package com.xwbing.domain.entity.vo;

import com.xwbing.domain.entity.model.ExpressTrace;
import lombok.Data;

import java.util.List;

/**
 * 创建时间: 2017/11/17 9:20
 * 作者: xiangwb
 * 说明: 快递信息展示
 */
@Data
public class ExpressInfoVo {
    /**
     * 快递代码
     */
    private String shipperCode;
    private boolean success;
    /**
     * 运单号
     */
    private String logisticCode;
    /**
     * 状态
     */
    private String state;
    /**
     * 物流信息
     */
    private List<ExpressTrace> traces;
    /**
     * 描述
     */
    private String describe;
}
