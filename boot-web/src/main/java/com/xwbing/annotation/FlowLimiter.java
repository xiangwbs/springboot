package com.xwbing.annotation;

import java.lang.annotation.*;

/**
 * @author xiangwb
 * @date 2019/5/24 19:06
 * @description 自定义限流注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface FlowLimiter {
    /**
     * 每秒往桶中放的令牌数
     *
     * @return
     */
    double permitsPerSecond();

    /**
     * 规定毫秒数中没有获取令牌，服务降级
     *
     * @return
     */
    long timeOut();
}
