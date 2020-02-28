package com.xwbing.domain.entity.pay.alipay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 说明: 支付宝退款参数
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:38
 * 作者:  xiangwb
 */
@Data
public class AliPayRefundParam {
    /**
     * 本系统订单号  订单号和支付宝交易号2选1
     */
    @JSONField(name = "out_trade_no")
    private String outTradeNo;
    /**
     * 支付宝交易号(推荐) 订单号和支付宝交易号2选1
     */
    @JSONField(name = "trade_no")
    private String tradeNo;
    /**
     * 退款请求号
     */
    @JSONField(name = "out_request_no")
    private String outRequestNo;
    /**
     * 退款金额
     */
    @JSONField(name = "refund_amount")
    private float refundAmount;
    /**
     * 退款的原因说明  可选
     */
    @JSONField(name = "refund_reason")
    private String refundReason;

    public AliPayRefundParam(String outRequestNo, String outTradeNo, float refundAmount, String refundReason) {
        this.outRequestNo = outRequestNo;
        this.outTradeNo = outTradeNo;
        this.refundAmount = refundAmount;
        this.refundReason = refundReason;
    }

    public AliPayRefundParam(String outRequestNo, String tradeNo, String refundReason, float refundAmount) {
        this.outRequestNo = outRequestNo;
        this.tradeNo = tradeNo;
        this.refundAmount = refundAmount;
        this.refundReason = refundReason;
    }
}
