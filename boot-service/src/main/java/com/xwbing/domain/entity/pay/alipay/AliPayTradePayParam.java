package com.xwbing.domain.entity.pay.alipay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 说明: 统一收单交易支付参数
 * 创建时间: 2017/5/10 17:34
 * 作者:  xiangwb
 */
@Data
public class AliPayTradePayParam {
    /**
     * 商户订单号
     */
    @JSONField(name = "out_trade_no")
    private String outTradeNo;
    /**
     * 支付场景
     * 条码支付:bar_code
     * 声波支付:wave_code
     */
    private String scene = "bar_code";
    /**
     * 支付授权码
     */
    @JSONField(name = "auth_code")
    private String authCode;
    /**
     * 订单标题
     */
    private String subject;
    /**
     * 订单描述(可选)
     */
    private String body;
    /**
     * 总金额
     * 单位为元，精确到小数点后两位
     * 取值范围[0.01,100000000]
     */
    @JSONField(name = "total_amount")
    private float totalAmount;
    /**
     * 支付超时时间
     * 1m～15d。m-分钟，h-小时，d-天
     */
    @JSONField(name = "timeout_express")
    private String timeoutExpress = "10m";
    /**
     * hb_fq_num 花呗分期数 仅支持传入 3、6、12
     * hb_fq_seller_percent 卖家承担收费比例 商家承担手续费传入 100，用户承担手续费传入 0，仅支持传入 100、0 两种
     */
    @JSONField(name = "extend_params")
    private String extendParams;

    public AliPayTradePayParam(String outTradeNo, String authCode, String subject, String body, float totalAmount) {
        this.outTradeNo = outTradeNo;
        this.authCode = authCode;
        this.subject = subject;
        this.body = body;
        this.totalAmount = totalAmount;
    }
}
