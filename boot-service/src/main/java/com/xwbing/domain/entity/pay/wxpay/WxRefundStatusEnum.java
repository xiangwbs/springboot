package com.xwbing.domain.entity.pay.wxpay;

/**
 * 说明: 微信退款状态
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/12 10:05
 * 作者:  xiangwb
 */

public enum WxRefundStatusEnum {
    SUCCESS("退款成功", "SUCCESS"),
    REFUNDCLOSE("退款关闭", "REFUNDCLOSE"),
    PROCESSING("退款处理中", "PROCESSING"),
    CHANGE("退款失败", "CHANGE");
    private String code;
    private String name;

    WxRefundStatusEnum(String name, String code) {
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
