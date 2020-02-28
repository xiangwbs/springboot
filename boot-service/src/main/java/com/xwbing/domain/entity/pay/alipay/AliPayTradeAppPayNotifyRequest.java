package com.xwbing.domain.entity.pay.alipay;

import lombok.Data;

/**
 * @author xiangwb
 * @date 20/2/9 16:20
 * 手机app支付接口2.0异步通知参数
 */
@Data
public class AliPayTradeAppPayNotifyRequest {
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
     * 开发者的app_id
     */
    private String app_id;
    /**
     * 编码格式
     */
    private String charset;
    /**
     * 接口版本
     */
    private String version;
    /**
     * 签名类型
     */
    private String sign_type;
    /**
     * 签名
     */
    private String sign;
    /**
     * 支付宝交易号
     */
    private String trade_no;
    /**
     * 商户订单号
     */
    private String out_trade_no;
    /**
     * 商户业务号
     */
    private String out_biz_no;
    /**
     * 买家支付宝用户号
     */
    private String buyer_id;
    /**
     * 买家支付宝账号
     */
    private String buyer_logon_id;
    /**
     * 卖家支付宝用户号
     */
    private String seller_id;
    /**
     * 卖家支付宝账号
     */
    private String seller_email;
    /**
     * 交易状态
     */
    private String trade_status;
    /**
     * 订单金额
     */
    private String total_amount;
    /**
     * 实收金额
     */
    private String receipt_amount;
    /**
     * 开票金额
     */
    private String invoice_amount;
    /**
     * 付款金额
     */
    private String buyer_pay_amount;
    /**
     * 集分宝金额
     */
    private String point_amount;
    /**
     * 总退款金额
     */
    private String refund_fee;
    /**
     * 订单标题
     */
    private String subject;
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
     * 交易退款时间
     */
    private String gmt_refund;
    /**
     * 交易结束时间
     */
    private String gmt_close;
    /**
     * 支付金额信息
     */
    private String fund_bill_list;
    /**
     * 回传参数
     */
    private String passback_params;
    /**
     * 优惠券信息
     */
    private String voucher_detail_list;
}
