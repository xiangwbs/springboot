package com.xwbing.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.UUID;

/**
 * 创建时间: 2018/4/25 14:48
 * 作者: xiangwb
 * 说明: 生产者
 */

//public class Sender implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
@Slf4j
@Component
public class Sender {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送信息到email队列
     *
     * @param msg
     */
    public void sendEmail(String[] msg) {
        send(msg, RabbitConstant.CONTROL_EXCHANGE, RabbitConstant.EMAIL_ROUTING_KEY);
    }

    /**
     * 发送信息到message队列
     *
     * @param msg
     */
    public void sendMessage(String[] msg) {
        send(msg, RabbitConstant.CONTROL_EXCHANGE, RabbitConstant.MESSAGE_ROUTING_KEY);
    }

    /**
     * 发送消息
     *
     * @param msg        消息
     * @param exchange   交换机
     * @param routingKey 路由键
     */
    private void send(String[] msg, String exchange, String routingKey) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        log.info("开始发送消息:{}", Arrays.toString(msg));
        //转换并发送消息,且等待消息者返回响应消息。
        Object response = rabbitTemplate.convertSendAndReceive(exchange, routingKey, msg, correlationId);
        if (response != null) {
            log.info("消费者响应:{}", response.toString());
        }
        log.info("{}消息发送结束", Arrays.toString(msg));
    }
}
