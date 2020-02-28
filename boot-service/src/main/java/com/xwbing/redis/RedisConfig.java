package com.xwbing.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 说明: redisConfig
 * 创建时间: 2017/5/5 16:44
 * 作者:  xiangwb
 */
@Configuration
@PropertySource("classpath:redis.properties")
public class RedisConfig {
    @Value("${maxTotal}")
    private Integer maxTotal;
    @Value("${maxIdle}")
    private Integer maxIdle;
    @Value("${minIdle}")
    private Integer minIdle;
    @Value("${host}")
    private String host;
    @Value("${port}")
    private Integer port;
    @Value("${timeout}")
    private Integer timeOut;
    @Value("${password}")
    private String password;

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        return config;
    }

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig config = jedisPoolConfig();
        if (StringUtils.isEmpty(password)) {
            password = null;
        }
        return new JedisPool(config, host, port, timeOut, password);
    }
}
