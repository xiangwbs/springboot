package com.xwbing.demo.DesignPattern.obServer.demo;

/**
 * @author xiangwb
 * @date 2020/3/6 21:41
 * 抽象观察者
 */
public interface ObServer {
    /**
     * 通知观察者消息
     *
     * @param message
     */
    void update(String message);
}
