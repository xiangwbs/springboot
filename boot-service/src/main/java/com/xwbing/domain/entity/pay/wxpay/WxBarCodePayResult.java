package com.xwbing.domain.entity.pay.wxpay;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 说明: 微信扫码支付接口结果
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:42
 * 作者:  xiangwb
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WxBarCodePayResult extends WxBaseResult {
    public static final String HAS_PAYED = "ORDERPAID";
    /**
     * 公众账号ID
     */
    private String appid;
    /**
     * 商户号
     */
    private String mchId;
    /**
     * 随机字符串
     */
    private String nonceStr;
    /**
     * 签名
     */
    private String sign;
    /**
     * 用户在商户appid 下的唯一标识
     */
    private String openId;
    /**
     * 用户是否关注公众账号，仅在公众账号类型支付有效，取值范围：Y或N;Y-关注;N-未关注
     */
    private String isSubscribe;
    /**
     * 支付类型为MICROPAY(即扫码支付)
     */
    private String tradeType;
    /**
     * 付款银行
     */
    private String bankType;
    /**
     * 订单金额
     */
    private int totalFee;
    /**
     * 现金支付金额
     */
    private int cashFee;
    /**
     * 微信支付订单号
     */
    private String transactionId;
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 支付完成时间
     */
    private String timeEnd;

    public WxBarCodePayResult(boolean isSuccess) {
        this.setSuccess(isSuccess);
    }
}
