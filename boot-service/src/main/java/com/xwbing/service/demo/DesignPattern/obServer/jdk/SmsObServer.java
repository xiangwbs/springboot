package com.xwbing.service.demo.DesignPattern.obServer.jdk;

import java.util.Observable;
import java.util.Observer;

/**
 * @author xiangwb
 * @date 2020/3/6 22:15
 */
public class SmsObServer implements Observer {
    @Override
    public void update(Observable o, Object arg) {
        System.out.println("用户下单成功，发送短信提醒内容:" + arg);

    }
}
