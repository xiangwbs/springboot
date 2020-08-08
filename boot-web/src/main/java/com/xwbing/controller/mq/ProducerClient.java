package com.xwbing.controller.mq;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.config.aliyun.rocketmq.MessageEvent;
import com.xwbing.config.aliyun.rocketmq.MessageOrderTypeEnum;
import com.xwbing.config.aliyun.rocketmq.OnsTemplate;

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
public class ProducerClient {
    private final OnsTemplate onsTemplate;

    @PostMapping("/mq/send")
    public void send(@RequestParam String key) {
        Msg message = Msg.builder().author("daofeng").title("send").content("同步发送").build();
        MessageEvent event = MessageEvent.builder().topic("rap_content").tag("OPEN_DETAIL").domain(message)
                .domainKey(key).build();
        onsTemplate.send(event);
    }

    @PostMapping("/mq/sendOrder")
    public void sendOrder(@RequestParam String key) {
        Msg message = Msg.builder().author("daofeng").title("sendOrder").content("同步发送顺序消息").build();
        MessageEvent event = MessageEvent.builder().topic("rap_content").tag("OPEN_DETAIL").domain(message)
                .domainKey(key).build();
        onsTemplate.sendOrder(event, MessageOrderTypeEnum.TAG);
    }

    @PostMapping("/mq/sendAsync")
    public void sendAsync(@RequestParam String key) {
        Msg message = Msg.builder().author("daofeng").title("sendAsync").content("异步发送").build();
        MessageEvent event = MessageEvent.builder().topic("rap_content").tag("CLOSE_DETAIL").domain(message)
                .domainKey(key).build();
        onsTemplate.sendAsync(event);
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
