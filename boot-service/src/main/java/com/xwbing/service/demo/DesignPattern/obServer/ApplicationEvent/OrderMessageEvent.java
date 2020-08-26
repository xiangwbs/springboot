package com.xwbing.service.demo.DesignPattern.obServer.ApplicationEvent;

import com.alibaba.fastjson.JSONObject;
import org.springframework.context.ApplicationEvent;

/**
 * @author xiangwb
 * @date 2020/3/6 22:36
 */
public class OrderMessageEvent extends ApplicationEvent {
    /**
     * 群发消息的内容
     */
    private JSONObject message;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public OrderMessageEvent(Object source, JSONObject message) {
        super(source);
        this.message = message;
    }

    public JSONObject getMessage() {
        return message;
    }

    public void setMessage(JSONObject message) {
        this.message = message;
    }
}
