package com.xwbing.demo.DesignPattern.obServer.jdk;

import java.util.Observable;

/**
 * @author xiangwb
 * @date 2020/3/6 22:21
 */
public class ObServerTest {
    public static void main(String[] args) {
        //创建具体主题
        Observable messageObservable = new MessageObservable();
        //注册观察者
        messageObservable.addObserver(new EmailObServer());
        messageObservable.addObserver(new SmsObServer());
        //群发消息
        messageObservable.notifyObservers("恭喜下单成功");
    }
}
