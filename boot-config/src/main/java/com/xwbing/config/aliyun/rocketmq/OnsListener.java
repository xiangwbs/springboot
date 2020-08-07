package com.xwbing.config.aliyun.rocketmq;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.filter.ExpressionType;

/**
 * @author daofeg
 * @version $
 * @since 2020年08月06日 20:54
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnsListener {
    String type() default ExpressionType.TAG;

    String topic();

    String groupId();

    String expression() default "*";
}