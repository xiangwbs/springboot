package com.xwbing.service.demo.designpattern.strategy;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xiangwb
 * @date 2020/3/6 18:18
 */
public enum DPayStrategyEnum {
    ALI_PAY("alipay", DAliPayStrategy.class.getSimpleName()),
    WX_PAY("wxpay", DWxPayStrategy.class.getSimpleName());
    private String payType;
    private String className;

    DPayStrategyEnum(String payType, String className) {
        this.payType = payType;
        this.className = className;
    }

    private static final Map<String, DPayStrategyEnum> map = Arrays.stream(DPayStrategyEnum
            .values()).collect(Collectors.toMap(DPayStrategyEnum::getPayType, Function.identity()));

    public static DPayStrategyEnum parse(String payType) {
        return map.get(payType);
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
