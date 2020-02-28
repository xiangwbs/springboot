package com.xwbing.config.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

/**
 * @author xiangwb
 * redis自动配置加载类
 * 提供外部可插拔式插件功能2种方式:
 * 1.在META-INF/spring.factories/EnableAutoConfiguration下配置RedisAutoConfiguration
 * 2.自定义@EnableRedis注解，使用@Import导入RedisAutoConfiguration
 */
@Configuration
@ConditionalOnProperty(prefix = RedisProperties.PREFIX, name = {"enabled"}, havingValue = "true")
@EnableConfigurationProperties(RedisProperties.class)
public class RedisAutoConfiguration {
    @Resource
    private RedisProperties redisProperties;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(redisProperties.getMaxTotal());
        config.setMaxIdle(redisProperties.getMaxIdle());
        config.setMinIdle(redisProperties.getMinIdle());
        String password = null;
        if (StringUtils.isNotEmpty(redisProperties.getPassword())) {
            password = redisProperties.getPassword();
        }
        return new JedisPool(config, redisProperties.getHost(), redisProperties.getPort(), redisProperties.getTimeout(), password);
    }

    @Bean
    @ConditionalOnMissingBean(RedisService.class)
    public RedisService redisService(JedisPool pool) {
        RedisService redisService = new RedisService();
        redisService.setJedisPool(pool);
        redisService.setPrefix(redisProperties.getPrefix());
        return redisService;
    }
}
