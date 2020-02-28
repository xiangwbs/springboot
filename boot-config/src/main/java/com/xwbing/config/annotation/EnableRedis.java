package com.xwbing.config.annotation;

import com.xwbing.config.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author xiangwb
 * 开启redis自动配置
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RedisAutoConfiguration.class)
public @interface EnableRedis {
}
