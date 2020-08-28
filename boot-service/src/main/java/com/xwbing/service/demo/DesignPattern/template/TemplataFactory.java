package com.xwbing.service.demo.DesignPattern.template;

import com.xwbing.starter.spring.ApplicationContextHelper;

/**
 * @author xiangwb
 * @date 2020/3/6 21:02
 */
public class TemplataFactory {
    public static AliPayCallbackTemplate getPayCallbackTemplate(String payType) {
        String className = PayCallbackTemplateEnum.parse(payType).getClassName();
        return ApplicationContextHelper.getBean(className, AliPayCallbackTemplate.class);
    }
}
