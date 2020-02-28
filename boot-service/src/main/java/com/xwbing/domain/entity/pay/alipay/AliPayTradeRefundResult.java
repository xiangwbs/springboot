package com.xwbing.domain.entity.pay.alipay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 说明: 支付宝退款结果
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:39
 * 作者:  xiangwb
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AliPayTradeRefundResult extends AliPayBaseResult {
    /**
     * 支付宝交易号
     */
    @JSONField(name = "trade_no")
    private String tradeNo;
    /**
     * 商户订单号
     */
    @JSONField(name = "out_trade_no")
    private String outTradeNo;
    /**
     * 用户的登录id
     */
    @JSONField(name = "buyer_logon_id")
    private String buyerLogonId;
    /**
     * 本次退款是否发生了资金变化  Y|N
     */
    @JSONField(name = "fund_change")
    private String fundChange;
    /**
     * 退款金额
     */
    @JSONField(name = "refund_fee")
    private String refundFee;
    /**
     * 退款支付时间
     */
    @JSONField(name = "gmt_refund_pay")
    private Date gmtRefundPay;
    /**
     * 买家在支付宝的用户id
     */
    @JSONField(name = "buyer_user_id")
    private String buyerUserId;

    public AliPayTradeRefundResult(boolean success) {
        this.setSuccess(success);
    }
}

