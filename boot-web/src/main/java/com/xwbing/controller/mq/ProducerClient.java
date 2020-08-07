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
 * @author daofeng
 * @version $Id$
 * @since 2020年08月06日 下午8:07
 */
@Slf4j
@AllArgsConstructor
@Api(tags = "producerClient", description = "mq发送")
@RestController
public class ProducerClient {
    private final OnsTemplate onsTemplate;

    @PostMapping("/mq/open")
    public void open(@RequestParam String title) {
        Msg message = Msg.builder().author("daofeng").title(title).content("同步打开").build();
        MessageEvent event = MessageEvent.builder().topic("rap_content").tag("OPEN_DETAIL").domain(message)
                .domainKey("111111").build();
        onsTemplate.send(event);
    }

    @PostMapping("/mq/orderOpen")
    public void orderOpen(@RequestParam String title) {
        Msg message = Msg.builder().author("daofeng").title(title).content("顺序打开").build();
        MessageEvent event = MessageEvent.builder().topic("rap_content").tag("OPEN_DETAIL").domain(message)
                .domainKey("222222").build();
        onsTemplate.orderSend(event, MessageOrderTypeEnum.TAG);
    }

    @PostMapping("/mq/close")
    public void close(@RequestParam String title) {
        Msg message = Msg.builder().author("daofeng").title(title).content("异步关闭").build();
        MessageEvent event = MessageEvent.builder().topic("rap_content").tag("CLOSE_DETAIL").domain(message)
                .domainKey("333333").build();
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
