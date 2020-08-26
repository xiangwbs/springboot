package com.xwbing.service.demo.DesignPattern.template;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xiangwb
 * @date 2020/3/6 18:18
 */
public enum PayCallbackTemplateEnum {
    ALI_PAY("alipay", AliPayCallbackTemplate.class.getSimpleName());
    private String payType;
    private String className;

    PayCallbackTemplateEnum(String payType, String className) {
        this.payType = payType;
        this.className = className;
    }

    private static final Map<String, PayCallbackTemplateEnum> map = Arrays.stream(PayCallbackTemplateEnum.values()).collect(Collectors.toMap(PayCallbackTemplateEnum::getPayType, Function.identity()));

    public static PayCallbackTemplateEnum parse(String payType) {
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
