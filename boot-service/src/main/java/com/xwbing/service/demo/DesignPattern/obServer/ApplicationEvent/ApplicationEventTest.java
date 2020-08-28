package com.xwbing.service.demo.DesignPattern.obServer.ApplicationEvent;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.starter.spring.ApplicationContextHelper;

/**
 * @author xiangwb
 * @date 2020/3/6 23:02
 */
public class ApplicationEventTest {
    public void publishEvent() {
        JSONObject msg = new JSONObject();
        msg.put("name", "xwb");
        msg.put("orderId", "123456");
        msg.put("msg", "下单成功");
        OrderMessageEvent orderMessageEvent = new OrderMessageEvent(this, msg);
        ApplicationContextHelper.publishEvent(orderMessageEvent);
    }
}
