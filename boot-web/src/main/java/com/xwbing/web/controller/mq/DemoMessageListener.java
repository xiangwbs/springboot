package com.xwbing.web.controller.mq;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.xwbing.starter.aliyun.rocketmq.OnsListener;
import com.xwbing.web.controller.mq.ProducerClient.Msg;

import lombok.extern.slf4j.Slf4j;

/**
 * 消息监听器示例
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年08月06日 下午7:11
 */
@Slf4j
@OnsListener(consumerGroup = "GID_dop_content", topic = "rap_content", expression = "OPEN_DETAIL||CLOSE_DETAIL")
@Component
public class DemoMessageListener implements MessageListener {

    @Override
    public Action consume(Message message, ConsumeContext context) {
        log.info("consume message:{}", message);
        final String body = new String(message.getBody(), StandardCharsets.UTF_8);
        final String tag = message.getTag();
        try {
            Msg msg = JSONObject.parseObject(body, Msg.class);
            log.info("consume event:{}", msg);
            switch (tag) {
                case "OPEN_DETAIL":
                    System.out.println(msg);
                    break;
                case "CLOSE_DETAIL":
                    System.out.println(msg);
                    break;
                default:
                    break;
            }
            return Action.CommitMessage;
        } catch (Exception e) {
            log.error("consume error with body:{}", body, e);
            return Action.ReconsumeLater;
        }
    }
}
