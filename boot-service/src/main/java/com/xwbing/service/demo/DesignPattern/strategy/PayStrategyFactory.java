package com.xwbing.service.demo.DesignPattern.strategy;

import com.xwbing.config.spring.ApplicationContextHelper;

/**
 * @author xiangwb
 * @date 2020/3/6 18:25
 * 工厂模式
 * BeanFactory
 * 创建对象 不需要自己实例化 使用工厂获取对象即可
 * 代码结构简单 扩展时 只需要改工厂类就行
 * 枚举或数据库维护工厂类型
 */
public class PayStrategyFactory {
    public static PayStrategy getPayStrategy(String payType) {
        String className = PayStrategyEnum.parse(payType).getClassName();
        return ApplicationContextHelper.getBean(className, PayStrategy.class);
    }
}
