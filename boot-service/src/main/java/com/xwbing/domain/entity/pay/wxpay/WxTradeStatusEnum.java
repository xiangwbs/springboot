package com.xwbing.domain.entity.pay.wxpay;

/**
 * 说明: 微信支付结果状态
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:44
 * 作者:  xiangwb
 */

public enum WxTradeStatusEnum {
    SUCCESS("支付成功", "SUCCESS"),
    REFUND("转入退款", "REFUND"),
    NOTPAY("未支付", "NOTPAY"),
    CLOSED("已关闭", "CLOSED"),
    REVOKED("已撤销（刷卡支付）", "REVOKED"),
    USERPAYING("用户支付中", "USERPAYING"),
    PAYERROR("支付失败", "PAYERROR");
    private String code;
    private String name;

    WxTradeStatusEnum(String name, String code) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
