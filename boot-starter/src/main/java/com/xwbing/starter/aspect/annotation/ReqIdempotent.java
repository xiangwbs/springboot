package com.xwbing.starter.aspect.annotation;

import java.lang.annotation.*;

/**
 * @author xiangwb
 * 解决接口幂等性 支持网络延迟和表单重复提交
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ReqIdempotent {
    /**
     * 值类型 header/param
     * @return
     */
    String type() default "header";
}
