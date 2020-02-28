package com.xwbing.rabbit;

/**
 * 创建时间: 2018/4/25 14:34
 * 作者: xiangwb
 * 说明: Rabbit常量
 */
public class RabbitConstant {
    /**
     * 邮件队列
     */
    public static final String EMAIL_QUEUE = "email";
    /**
     * 短信队列
     */
    public static final String MESSAGE_QUEUE = "message";
    /**
     * 邮件队列路由键（*表示一个词,#表示零个或多个词）
     */
    public static final String EMAIL_ROUTING_KEY = "email.key";
    /**
     * 短信队列路由键
     */
    public static final String MESSAGE_ROUTING_KEY = "message.key";
    /**
     * 交换机
     */
    public static final String CONTROL_EXCHANGE = "control.exchange";
}
