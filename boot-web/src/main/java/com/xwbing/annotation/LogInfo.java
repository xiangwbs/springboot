package com.xwbing.annotation;

import java.lang.annotation.*;

/**
 * 说明: 自定义日志信息注解
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface LogInfo {
    String value() default "";
}
