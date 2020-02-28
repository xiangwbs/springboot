package com.xwbing.domain.entity.pay.wxpay;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * 说明: 微信扫码支付接口参数
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:41
 * 作者:  xiangwb
 */
@Data
public class WxBarCodePayParam {
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 请求ip
     */
    private String spblillCreateIp;
    /**
     * 授权码 扫码支付授权码，设备读取用户微信中的条码或者二维码信息
     */
    private String authCode;
    /**
     * 商品描述
     */
    private String body;
    /**
     * 订单金额 订单总金额，单位为分，只能为整数，详见支付金额
     */
    @JSONField(name = "total_fee")
    private int totalFee;

    public WxBarCodePayParam(String outTradeNo, String spblillCreateIp, String authCode, String body, int totalFee) {
        this.outTradeNo = outTradeNo;
        this.spblillCreateIp = spblillCreateIp;
        this.authCode = authCode;
        this.body = body;
        this.totalFee = totalFee;
    }
}
