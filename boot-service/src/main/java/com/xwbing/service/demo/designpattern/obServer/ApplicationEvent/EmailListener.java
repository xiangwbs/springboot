package com.xwbing.service.demo.designpattern.obServer.ApplicationEvent;

import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author xiangwb
 * @date 2020/3/6 22:39
 */
@Component
public class EmailListener implements ApplicationListener<OrderMessageEvent> {
    @Async
    @Override
    public void onApplicationEvent(OrderMessageEvent event) {
        System.out.println("开始发送邮件消息内容:" + event.getMessage().toJSONString());
    }
}
