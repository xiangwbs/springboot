package com.xwbing.starter.redis;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * @author xiangwb
 * 开启redis自动配置
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ RedisAutoConfiguration.class, RedisTemplateConfiguration.class })
public @interface EnableRedis {
}