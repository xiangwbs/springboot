package com.xwbing.config.aliyun.rocketmq;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeg
 * @version $
 * @since 2020年08月06日 20:54
 */
@Slf4j
public class OnsTemplate {
    @Resource
    private ProducerBean producer;
    @Resource
    private OrderProducerBean orderProducer;

    /**
     * 同步发送
     *
     * @param event
     *
     * @return
     */
    public SendResult send(MessageEvent event) {
        Message message = getMessage(event);
        SendResult result = this.producer.send(message);
        log.info("send message success. {}", result.toString());
        return result;
    }

    /**
     * 单向发送
     * 发送特点为发送方只负责发送消息，不等待服务器回应且没有回调函数触发，即只发送请求不等待应答。
     * 此方式发送消息的过程耗时非常短，一般在微秒级别。适用于某些耗时非常短，但对可靠性要求并不高的场景，例如日志收集。
     *
     * @param event
     */
    public void sendOneway(MessageEvent event) {
        Message message = getMessage(event);
        this.producer.sendOneway(message);
        log.info("send message success. ");
    }

    /**
     * 同步发送(带延迟时间)
     *
     * @param event
     * @param delay 毫秒
     *
     * @return
     */
    public SendResult send(MessageEvent event, long delay) {
        Message message = getMessage(event);
        message.setStartDeliverTime(System.currentTimeMillis() + delay);
        SendResult result = this.producer.send(message);
        log.info("send message success. {}", result.toString());
        return result;
    }

    /**
     * 同步发送(延迟定时发送, 请注意保证时间正确)
     *
     * @param event
     * @param date
     *
     * @return
     */
    public SendResult send(MessageEvent event, Date date) {
        long delay = getDelay(date);
        return send(event, delay);
    }

    /**
     * 同步发送(延迟定时发送, 请注意保证时间正确)
     *
     * @param event
     * @param date
     *
     * @return
     */
    public SendResult send(MessageEvent event, LocalDateTime date) {
        long delay = getDelay(date);
        return send(event, delay);
    }

    /**
     * 异步发送
     *
     * @param event
     */
    public void sendAsync(MessageEvent event) {
        Message message = getMessage(event);
        this.producer.sendAsync(message, new DefaultSendCallback());
    }

    /**
     * 异步发送(带延迟时间)
     *
     * @param event
     * @param delay 毫秒
     */
    public void sendAsync(MessageEvent event, long delay) {
        Message message = getMessage(event);
        message.setStartDeliverTime(System.currentTimeMillis() + delay);
        this.producer.sendAsync(message, new DefaultSendCallback());
    }

    /**
     * 异步发送(带延迟时间)
     *
     * @param event
     * @param delay
     * @param callback
     */
    public void sendAsync(MessageEvent event, long delay, SendCallback callback) {
        Message message = getMessage(event);
        message.setStartDeliverTime(System.currentTimeMillis() + delay);
        this.producer.sendAsync(message, callback);
    }

    /**
     * 异步发送(延迟定时发送, 请注意保证时间正确)
     *
     * @param event
     * @param date
     * @param callback
     */
    public void sendAsync(MessageEvent event, Date date, SendCallback callback) {
        long delay = getDelay(date);
        sendAsync(event, delay, callback);
    }

    /**
     * 异步发送(延迟定时发送, 请注意保证时间正确)
     *
     * @param event
     * @param date
     * @param callback
     */
    public void sendAsync(MessageEvent event, LocalDateTime date, SendCallback callback) {
        long delay = getDelay(date);
        sendAsync(event, delay, callback);
    }

    /**
     * 同步发送顺序消息
     *
     * @param event
     *
     * @return
     */
    public SendResult orderSend(MessageEvent event) {
        return orderSend(event, MessageOrderTypeEnum.GLOBAL);
    }

    /**
     * 同步发送顺序消息
     *
     * @param event
     * @param orderType
     *
     * @return
     */
    public SendResult orderSend(MessageEvent event, MessageOrderTypeEnum orderType) {
        String shardingKey;
        switch (orderType) {
            case GLOBAL:
                shardingKey = "#global#";
                break;
            case TOPIC:
                shardingKey = "#" + event.getTopic() + "#";
                break;
            case TAG:
                shardingKey = "#" + event.getTopic() + "#" + event.getTag() + "#";
                break;
            default:
                shardingKey = "#global#";
                break;
        }
        return orderSend(event, shardingKey);
    }

    /**
     * 同步发送顺序消息
     *
     * @param event
     * @param shardingKey 分区顺序消息中区分不同分区的关键字段，sharding key 于普通消息的 key 是完全不同的概念。
     *         全局顺序消息，该字段可以设置为任意非空字符串
     *
     * @return
     */
    private SendResult orderSend(MessageEvent event, String shardingKey) {
        Message message = getMessage(event);
        SendResult result = this.orderProducer.send(message, shardingKey);
        log.info("send message success. {}", result.toString());
        return result;
    }

    private Message getMessage(MessageEvent event) {
        if (event == null) {
            throw new RuntimeException("event is null.");
        }
        log.info("start to send message. [topic:{},tag:{}]", event.getTopic(), event.getTag());
        if (StringUtils.isEmpty(event.getTopic()) || event.getDomain() == null) {
            throw new RuntimeException("topic or body is null.");
        }
        final byte[] body = JSONObject.toJSONString(event.getDomain()).getBytes(StandardCharsets.UTF_8);
        Message message = new Message(event.getTopic(), event.getTag(), body);
        message.setKey(event.generateTxId());
        return message;
    }

    private long getDelay(Date date) {
        Date now = new Date();
        long delay = date.getTime() - now.getTime();
        if (delay <= 0) {
            throw new RuntimeException("消息发送时间不能小于当前时间");
        }
        return delay;
    }

    private long getDelay(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();
        ZoneId zone = ZoneId.systemDefault();
        long delay = date.atZone(zone).toInstant().toEpochMilli() - now.atZone(zone).toInstant().toEpochMilli();
        if (delay <= 0) {
            throw new RuntimeException("消息发送时间不能小于当前时间");
        }
        return delay;
    }
}