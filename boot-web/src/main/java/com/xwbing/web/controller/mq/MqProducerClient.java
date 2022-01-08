package com.xwbing.web.controller.mq;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.starter.aliyun.rocketmq.MessageEvent;
import com.xwbing.starter.aliyun.rocketmq.MessageOrderTypeEnum;
import com.xwbing.starter.aliyun.rocketmq.OnsTemplate;
import com.xwbing.web.controller.mq.MqConstant.Tag;
import com.xwbing.web.controller.mq.MqConstant.Topic;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息发送示例
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年08月06日 下午8:07
 */
@Slf4j
@AllArgsConstructor
@Api(tags = "producerClient", description = "mq发送demo")
@RestController
public class MqProducerClient {
    private final OnsTemplate onsTemplate;

    @PostMapping("/mq/send")
    public void send(@RequestParam String key) {
        Msg message = Msg.builder().author("daofeng").title("send").content("同步发送").build();
        MessageEvent event = MessageEvent.builder().topic(Topic.TOPIC1).tag(Tag.TAG1).data(message).key(key).build();
        onsTemplate.send(event);
    }

    @PostMapping("/mq/sendAsync")
    public void sendAsync(@RequestParam String key) {
        Msg message = Msg.builder().author("daofeng").title("sendAsync").content("异步发送").build();
        MessageEvent event = MessageEvent.builder().topic(Topic.TOPIC1).tag(Tag.TAG1).data(message).key(key).build();
        onsTemplate.sendAsync(event);
    }

    @PostMapping("/mq/sendOrder")
    public void sendOrder(@RequestParam String key) {
        Msg message = Msg.builder().author("daofeng").title("sendOrder").content("同步发送顺序消息").build();
        MessageEvent event = MessageEvent.builder().topic(Topic.TOPIC1).tag(Tag.TAG1).data(message).key(key).build();
        onsTemplate.sendOrder(event, MessageOrderTypeEnum.TAG);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Msg {
        private String author;
        private String title;
        private String content;
    }
}
