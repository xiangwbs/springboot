package com.xwbing.starter.aliyun.rocketmq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * @author daofeng
 * @version $
 * @since 2020年08月06日 17:03
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(OnsConfiguration.class)
public @interface EnableRocketMQ {
}
