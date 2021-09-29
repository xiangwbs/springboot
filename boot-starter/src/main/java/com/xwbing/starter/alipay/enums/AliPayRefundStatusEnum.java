package com.xwbing.starter.alipay.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 说明: 支付宝退款状态
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:39
 * 作者:  xiangwb
 */
public enum AliPayRefundStatusEnum {
    //@formatter:off
    REFUND_PROCESSING("REFUND_PROCESSING","退款处理中"),
    TRADE_CLOSED("TRADE_CLOSED","退款处理成功"),
    TRADE_SUCCESS( "TRADE_SUCCESS","退款失败");
    private String code;
    private String name;

    AliPayRefundStatusEnum( String code,String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
    private static final Map<String, AliPayRefundStatusEnum> ENUM_MAP = Arrays.stream(AliPayRefundStatusEnum.values())
            .collect(Collectors.toMap(AliPayRefundStatusEnum::getCode, Function.identity()));

    public static AliPayRefundStatusEnum parse(String code) {
        return ENUM_MAP.get(code);
    }
}
