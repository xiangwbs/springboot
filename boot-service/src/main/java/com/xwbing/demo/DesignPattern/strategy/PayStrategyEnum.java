package com.xwbing.demo.DesignPattern.strategy;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xiangwb
 * @date 2020/3/6 18:18
 */
public enum PayStrategyEnum {
    ALI_PAY("alipay", AliPayStrategy.class.getSimpleName()),
    WX_PAY("wxpay", WxPayStrategy.class.getSimpleName());
    private String payType;
    private String className;

    PayStrategyEnum(String payType, String className) {
        this.payType = payType;
        this.className = className;
    }

    private static final Map<String, PayStrategyEnum> map = Arrays.stream(PayStrategyEnum.values()).collect(Collectors.toMap(PayStrategyEnum::getPayType, Function.identity()));

    public static PayStrategyEnum parse(String payType) {
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
