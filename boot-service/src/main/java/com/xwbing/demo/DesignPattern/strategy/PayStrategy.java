package com.xwbing.demo.DesignPattern.strategy;

/**
 * @author xiangwb
 * @date 2020/3/6 18:14
 * 策略模式
 * spring读取资源信息:org.springframework.core.io.ResourceLoader.Resource
 * 解决多重if else判断的问题 提高扩展性
 */
public interface PayStrategy {
    String toPay();
}
