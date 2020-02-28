package com.xwbing.config.annotation;

import java.lang.annotation.*;

/**
 * @author xiangwb
 * 解决接口幂等性 支持网络延迟和表单重复提交
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Idempotent {
    String type() default "header";
}
