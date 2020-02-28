package com.xwbing.domain.entity.pay.alipay;

import lombok.Data;

/**
 * @author xiangwb
 * @date 20/2/9 16:20
 * 手机app支付接口1.0异步通知参数
 */
@Data
public class MobileSecurityPayNotifyRequest {
    /**
     * 通知时间
     */
    private String notify_time;
    /**
     * 通知类型
     */
    private String notify_type;
    /**
     * 通知校验ID
     */
    private String notify_id;
    /**
     * 签名方式(RSA)
     */
    private String sign_type;
    /**
     * 签名
     */
    private String sign;
    /**
     * 商户网站唯一订单号
     */
    private String out_trade_no;
    /**
     * 商品名称
     */
    private String subject;
    /**
     * 支付类型
     */
    private String payment_type;
    /**
     * 支付宝交易号
     */
    private String trade_no;
    /**
     * 交易状态
     */
    private String trade_status;
    /**
     * 卖家支付宝用户号
     */
    private String seller_id;
    /**
     * 卖家支付宝账号
     */
    private String seller_email;
    /**
     * 买家支付宝用户号
     */
    private String buyer_id;
    /**
     * 买家支付宝账号
     */
    private String buyer_email;
    /**
     * 交易金额
     */
    private String total_fee;
    /**
     * 购买数量
     */
    private String quantity;
    /**
     * 商品单价
     */
    private String price;
    /**
     * 商品描述
     */
    private String body;
    /**
     * 交易创建时间
     */
    private String gmt_create;
    /**
     * 交易付款时间
     */
    private String gmt_payment;
    /**
     * 是否调整总价
     */
    private String is_total_fee_adjust;
    /**
     * 是否使用红包买家
     */
    private String use_coupon;
    /**
     * 折扣
     */
    private String discount;
    /**
     * 退款状态
     */
    private String refund_status;
    /**
     * 退款时间
     */
    private String gmt_refund;
}
