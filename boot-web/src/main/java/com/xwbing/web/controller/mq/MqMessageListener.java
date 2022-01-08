package com.xwbing.web.controller.mq;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.xwbing.starter.aliyun.rocketmq.OnsListener;
import com.xwbing.web.controller.mq.MqConstant.Group;
import com.xwbing.web.controller.mq.MqConstant.Tag;
import com.xwbing.web.controller.mq.MqConstant.Topic;
import com.xwbing.web.controller.mq.MqProducerClient.Msg;

import lombok.extern.slf4j.Slf4j;

/**
 * 消息监听器示例
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年08月06日 下午7:11
 */
@Slf4j
@OnsListener(consumerGroup = Group.GID1, topic = Topic.TOPIC1, expression = Tag.TAG1 + "||" + Tag.TAG2)
@Component
public class MqMessageListener implements MessageListener {
    @Override
    public Action consume(Message message, ConsumeContext context) {
        String body = new String(message.getBody(), StandardCharsets.UTF_8);
        String tag = message.getTag();
        log.info("mqMessageListener key:{} msgId:{} data:{}", message.getKey(), message.getMsgID(), body);
        try {
            Msg msg = JSONObject.parseObject(body, Msg.class);
            switch (tag) {
                case Tag.TAG1:
                    System.out.println(msg);
                    break;
                case Tag.TAG2:
                    System.out.println(msg);
                    break;
                default:
                    break;
            }
            return Action.CommitMessage;
        } catch (Exception e) {
            log.error("mqMessageListener key:{} msgId:{} error", message.getKey(), message.getMsgID(), tag, e);
            return Action.ReconsumeLater;
        }
    }
}
