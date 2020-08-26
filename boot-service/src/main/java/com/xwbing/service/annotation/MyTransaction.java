package com.xwbing.service.annotation;

import java.lang.annotation.*;

/**
 * @author xiangwb
 * @date 2020/3/6 13:17
 * 自定义事务注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface MyTransaction {
}
