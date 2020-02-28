package com.xwbing.domain.entity.pay.wxpay;

import lombok.Data;

/**
 * 说明: 微信退款参数
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:43
 * 作者:  xiangwb
 */
@Data
public class WxRefundParam {
    /**
     * 微信订单号(推荐)   微信订单号和商户订单号2选1
     */
    private String transactionId;
    /**
     * 商户订单号  微信订单号和商户订单号2选1
     */
    private String outTradeNo;
    /**
     * 商户退款单号
     */
    private String outRefundNo;
    /**
     * 订单金额
     */
    private int totalFee;
    /**
     * 退款金额
     */
    private int refundFee;
    /**
     * 操作员账号
     */
    private String opUserId;

    public WxRefundParam(String outTradeNo, String outRefundNo, int totalFee, int refundFee, String opUserId) {
        this.outTradeNo = outTradeNo;
        this.outRefundNo = outRefundNo;
        this.totalFee = totalFee;
        this.refundFee = refundFee;
        this.opUserId = opUserId;
    }

    public WxRefundParam(String transactionId, String outRefundNo, String opUserId, int totalFee, int refundFee) {
        this.transactionId = transactionId;
        this.outRefundNo = outRefundNo;
        this.totalFee = totalFee;
        this.refundFee = refundFee;
        this.opUserId = opUserId;
    }
}

