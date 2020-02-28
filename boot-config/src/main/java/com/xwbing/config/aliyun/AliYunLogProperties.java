package com.xwbing.config.aliyun;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xiangwb
 * aliyun属性加载配置类
 */
@Data
@ConfigurationProperties(prefix = AliYunLogProperties.PREFIX)
public class AliYunLogProperties {
    public static final String PREFIX = "boot.aliYun";
    /**
     * 开启aliYunLog
     */
    private Boolean enabled;
    /**
     * 阿里云日志
     */
    private final AliYunLogProperties.Log log = new AliYunLogProperties.Log();
    /**
     * 钉钉机器人
     */
    private final AliYunLogProperties.DingTalk dingTalk = new AliYunLogProperties.DingTalk();

    @Data
    public static class Log {
        private String accessId;
        private String accessKey;
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

    @Data
    public static class DingTalk {
        /**
         * 钉钉机器人地址
         */
        private String webHook;
        /**
         * 钉钉消息安全设置:加签(选填)
         */
        private String secret;
    }
}
