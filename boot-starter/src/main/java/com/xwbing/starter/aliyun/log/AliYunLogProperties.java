package com.xwbing.starter.aliyun.log;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * aliyun属性加载配置类
 *
 * @author xiangwb
 */
@Data
@ConfigurationProperties(prefix = AliYunLogProperties.PREFIX)
public class AliYunLogProperties {
    public static final String PREFIX = "boot.aliyun.log";
    private String accessId;
    private String accessSecret;
    /**
     * 华东1(杭州)
     */
    private String endpoint;
    /**
     * 阿里云上创建的项目名称
     */
    private String project;
    /**
     * 建议填本项目名称
     */
    private String topic;
    /**
     * 日志库名称
     */
    private String logStore;
}