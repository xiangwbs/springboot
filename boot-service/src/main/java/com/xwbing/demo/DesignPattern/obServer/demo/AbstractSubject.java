package com.xwbing.demo.DesignPattern.obServer.demo;

/**
 * @author xiangwb
 * @date 2020/3/6 21:43
 * 抽象主题
 */
public interface AbstractSubject {
    /**
     * 注册观察者
     *
     * @param obServer
     */
    void addObServer(ObServer obServer);

    /**
     * 移除观察者
     *
     * @param obServer
     */
    void removeObServer(ObServer obServer);


    /**
     * 通知消息
     */
    void notifyObServer(String message);

}
