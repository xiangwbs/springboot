package com.xwbing.starter.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
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
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, Object> objectRedisTemplate(RedisConnectionFactory redisConnectionFactory,
            StringRedisTemplate stringRedisTemplate) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
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
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    // @Bean(name = "redissonClient")
    // public RedissonClient getRedissonClient() {
    //     Config config = new Config();
    //     //cluster方式至少6个节点(3主3从，3主做sharding，3从用来保证主宕机后可以高可用)
    //     config.useClusterServers()
    //             .addNodeAddress("redis://192.168.8.127:6381")
    //             .addNodeAddress("redis://192.168.8.128:6381")
    //             .addNodeAddress("redis://192.168.8.129:6381")
    //             .addNodeAddress("redis://192.168.8.127:6380")
    //             .addNodeAddress("redis://192.168.8.128:6380")
    //             .addNodeAddress("redis://192.168.8.129:6380");
    //     return Redisson.create(config);
    // }

    @Bean(name = "redissonClient")
    public RedissonClient getRedissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        return Redisson.create(config);
    }
}