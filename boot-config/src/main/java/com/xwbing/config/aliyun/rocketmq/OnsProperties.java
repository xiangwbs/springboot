package com.xwbing.config.aliyun.rocketmq;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author daofeg
 * @version $
 * @since 2020年08月06日 20:54
 */
@Data
@ConfigurationProperties(prefix = OnsProperties.PREFIX)
public class OnsProperties {
    public static final String PREFIX = "boot.aliyun.ons";
    /**
     * 阿里云身份验证 AccessKeyId，在阿里云用户信息管理控制台获取。
     */
    private String username;
    /**
     * 阿里云身份验证 AccessKeySecret，在阿里云用户信息管理控制台获取。
     */
    private String password;
    /**
     * 您的消息队列RocketMQ版 实例的TCP 接入点
     */
    private String nameServerAddress;
    /**
     * 您在消息队列RocketMQ版 控制台上创建的 Group ID
     */
    private String producerGroupId;
}