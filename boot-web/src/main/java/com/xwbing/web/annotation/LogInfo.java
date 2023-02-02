package com.xwbing.web.annotation;

import java.lang.annotation.*;

/**
 * 说明: 自定义日志信息注解
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 16:36
 *
 * @author xwbing
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface LogInfo {
    String type() default "";
}