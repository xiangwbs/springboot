package com.xwbing.domain.entity.pay.alipay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 说明: 支付宝扫码支付参数
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:34
 * 作者:  xiangwb
 */
@Data
public class AliPayBarCodePayParam {
    /**
     * 本系统订单号
     */
    @JSONField(name = "out_trade_no")
    private String outTradeNo;
    /**
     * 条形码 授权
     */
    @JSONField(name = "auth_code")
    private String authCode;
    /**
     * 支付场景  条码支付或者是声波支付
     */
    private String scene;
    /**
     * 订单标题	  商品名称
     */
    private String subject;
    /**
     * 总金额
     */
    @JSONField(name = "total_amount")
    private float totalAmount;

    public AliPayBarCodePayParam(String outTradeNo, String authCode, String subject, float totalAmount) {
        this.outTradeNo = outTradeNo;
        this.authCode = authCode;
        this.subject = subject;
        this.totalAmount = totalAmount;
    }
}
