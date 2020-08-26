package com.xwbing.service.demo.DesignPattern.obServer.jdk;

import java.util.Observable;

/**
 * @author xiangwb
 * @date 2020/3/6 22:11
 */
public class MessageObservable extends Observable {
    @Override
    public void notifyObservers(Object arg) {
        //修改状态为可群发
        super.setChanged();
        //调用父类的notifyObservers,群发消息
        super.notifyObservers(arg);
    }
}
