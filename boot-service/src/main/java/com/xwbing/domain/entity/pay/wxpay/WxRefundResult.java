package com.xwbing.domain.entity.pay.wxpay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 说明: 退款返回结果
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:44
 * 作者:  xiangwb
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WxRefundResult extends WxBaseResult {
    /**
     * 微信分配的公众账号ID
     */
    private String appid;
    /**
     * 微信支付分配的商户号
     */
    @JSONField(name = "mch_id")
    private String mchId;
    /**
     * 随机字符串
     */
    @JSONField(name = "nonce_str")
    private String nonceStr;
    /**
     * 签名
     */
    @JSONField(name = "sign")
    private String sign;
    /**
     * 微信订单号
     */
    @JSONField(name = "transaction_id")
    private String transactionId;
    /**
     * 商户订单号
     */
    @JSONField(name = "out_trade_no")
    private String outTradeNo;
    /**
     * 商户退款单号
     */
    @JSONField(name = "out_refund_no")
    private String outRefundNo;
    /**
     * 微信退款单号
     */
    @JSONField(name = "refund_id")
    private String refundId;
    /**
     * 退款金额 单位分
     */
    @JSONField(name = "refund_fee")
    private int refundFee;
    /**
     * 订单总金额  单位分
     */
    @JSONField(name = "total_fee")
    private int totalFee;
    /**
     * 现金支付金额  单位分
     */
    @JSONField(name = "cash_fee")
    private int cashFee;

    public WxRefundResult(boolean isSuccess) {
        this.setSuccess(isSuccess);
    }
}
