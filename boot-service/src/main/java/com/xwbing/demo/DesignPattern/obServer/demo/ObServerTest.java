package com.xwbing.demo.DesignPattern.obServer.demo;

/**
 * @author xiangwb
 * @date 2020/3/6 22:00
 */
public class ObServerTest {
    public static void main(String[] args) {
        //创建具体主题
        AbstractSubject subject = new WeChatSubject();
        //开始注册或添加观察者
        subject.addObServer(new UserObServer("小王"));
        subject.addObServer(new UserObServer("小明"));
        //群发消息
        subject.notifyObServer("开学了");
    }
}
