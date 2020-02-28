package com.xwbing.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xiangwb
 * redis属性加载配置类
 */
@Data
@ConfigurationProperties(prefix = RedisProperties.PREFIX)
public class RedisProperties {
    public static final String PREFIX = "boot.redis";
    /**
     * 开启redis
     */
    private Boolean enabled;
    /**
     * 本项目缓存前缀
     */
    private String prefix;
    /**
     * 最大连接数
     */
    private Integer maxTotal;
    /**
     * 最大空闲连接数
     */
    private Integer maxIdle;
    /**
     * 最小空闲连接数
     */
    private Integer minIdle;
    /**
     * ip
     */
    private String host;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 超时时间(毫秒)
     */
    private Integer timeout;
    /**
     * 密码
     */
    private String password;
}
