package com.xwbing.starter.aliyun.rocketmq;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;

import lombok.extern.slf4j.Slf4j;

/**
 * Producer生产消息模板
 *
 * @author daofeg
 * @version $
 * @since 2020年08月06日 20:54
 */
@Slf4j
public class OnsTemplate {
    private final ProducerBean producer;
    private final OrderProducerBean orderProducer;

    public OnsTemplate(ProducerBean producer, OrderProducerBean orderProducer) {
        this.producer = producer;
        this.orderProducer = orderProducer;
    }
    // ---------------------- 同步发送 ----------------------

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
    }

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
        log.info("onsTemplate send key:{} msgId:{}", message.getKey(), result.getMessageId());
        return result;
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
        log.info("onsTemplate send key:{} msgId:{} delay:{}", message.getKey(), result.getMessageId(), delay);
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
    // ---------------------- 异步发送 ----------------------

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
     * @param delay 毫秒
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
    // ---------------------- 同步发送顺序消息 ----------------------

    /**
     * 同步发送顺序消息
     *
     * @param event
     *
     * @return
     */
    public SendResult sendOrder(MessageEvent event) {
        return sendOrder(event, MessageOrderTypeEnum.GLOBAL);
    }

    /**
     * 同步发送顺序消息
     *
     * shardingKey:
     * 分区顺序消息中区分不同分区的关键字段，shardingKey于普通消息的key是完全不同的概念。
     * 全局顺序消息，该字段可以设置为任意非空字符串
     *
     * @param event
     * @param orderType
     *
     * @return
     */
    public SendResult sendOrder(MessageEvent event, MessageOrderTypeEnum orderType) {
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
        return sendOrder(event, shardingKey);
    }

    /**
     * 同步发送顺序消息
     *
     * @param event
     * @param shardingKey
     *
     * @return
     */
    private SendResult sendOrder(MessageEvent event, String shardingKey) {
        Message message = getMessage(event);
        SendResult result = this.orderProducer.send(message, shardingKey);
        log.info("onsTemplate sendOrder key:{} msgId:{} shardingKey:{}", message.getKey(), result.getMessageId(),
                shardingKey);
        return result;
    }

    private Message getMessage(MessageEvent event) {
        if (event == null) {
            throw new RuntimeException("onsTemplate getMessage event is null");
        }
        if (StringUtils.isEmpty(event.getTopic()) || event.getData() == null) {
            throw new RuntimeException("onsTemplate getMessage topic or data is null");
        }
        final byte[] body = JSONObject.toJSONString(event.getData()).getBytes(StandardCharsets.UTF_8);
        Message message = new Message(event.getTopic(), event.getTag(), body);
        message.setKey(event.getKey());
        log.info("onsTemplate getMessage topic:{} tag:{} key:{} data:{}", event.getTopic(), event.getTag(),
                message.getKey(), event.getData());
        return message;
    }

    private long getDelay(Date date) {
        Date now = new Date();
        long delay = date.getTime() - now.getTime();
        if (delay <= 0) {
            throw new RuntimeException("onsTemplate deliverTime cannot be less than the current time");
        }
        return delay;
    }

    private long getDelay(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();
        ZoneId zone = ZoneId.systemDefault();
        long delay = date.atZone(zone).toInstant().toEpochMilli() - now.atZone(zone).toInstant().toEpochMilli();
        if (delay <= 0) {
            throw new RuntimeException("onsTemplate deliverTime cannot be less than the current time");
        }
        return delay;
    }
}