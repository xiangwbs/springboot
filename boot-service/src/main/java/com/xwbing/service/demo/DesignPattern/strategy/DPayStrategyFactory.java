package com.xwbing.service.demo.DesignPattern.strategy;

import com.xwbing.starter.spring.ApplicationContextHelper;

/**
 * @author xiangwb
 * @date 2020/3/6 18:25
 * 工厂模式
 * BeanFactory
 * 创建对象 不需要自己实例化 使用工厂获取对象即可
 * 代码结构简单 扩展时 只需要改工厂类就行
 * 枚举或数据库维护工厂类型
 */
public class DPayStrategyFactory {
    public static DPayStrategy getPayStrategy(String payType) {
        String className = DPayStrategyEnum.parse(payType).getClassName();
        return ApplicationContextHelper.getBean(className, DPayStrategy.class);
    }
}
