package com.xwbing.starter.aspect.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 频率注解
 *
 * @author daofeng
 * @version $id$
 * @since 2020-08-03 13:59
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Limit {
    /**
     * 超时时间 seconds
     */
    int timeout() default 10;
}
