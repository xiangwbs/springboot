package com.xwbing.starter.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import com.aliyun.openservices.shade.com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月17日 下午5:14
 */
@ConditionalOnProperty(prefix = "spring.redis", name = "host")
@Configuration
public class RedisTemplateConfiguration {
    @Bean
    @ConditionalOnMissingBean(RedisTemplate.class)
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory,
            StringRedisTemplate stringRedisTemplate) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        // 配置连接工厂
        template.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<String> stringSerializer = stringRedisTemplate.getStringSerializer();
        //key和hashKey的序列化采用StringRedisSerializer
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        //value和hashValue序列化采用fastJsonRedisSerializer
        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);
        template.setValueSerializer(fastJsonRedisSerializer);
        template.setHashValueSerializer(fastJsonRedisSerializer);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean(StringRedisTemplate.class)
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}