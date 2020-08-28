package com.xwbing.starter.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * redis自动配置加载类
 * 提供外部可插拔式插件功能2种方式:
 * 1.在META-INF/spring.factories/EnableAutoConfiguration下配置RedisAutoConfiguration
 * 2.自定义@EnableRedis注解，使用@Import导入RedisAutoConfiguration
 *
 * @author xiangwb
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisAutoConfiguration {
    private final RedisProperties redisProperties;

    public RedisAutoConfiguration(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(redisProperties.getPool().getMaxTotal());
        config.setMaxIdle(redisProperties.getPool().getMaxIdle());
        config.setMinIdle(redisProperties.getPool().getMinIdle());
        String password = null;
        if (StringUtils.isNotEmpty(redisProperties.getPassword())) {
            password = redisProperties.getPassword();
        }
        return new JedisPool(config, redisProperties.getHost(), redisProperties.getPort(), redisProperties.getTimeout(),
                password);
    }

    @Bean
    @ConditionalOnBean(JedisPool.class)
    public RedisService redisService(JedisPool pool) {
        return new RedisService(pool, redisProperties.getPrefix());
    }
}
