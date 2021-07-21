package com.xwbing.starter.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验签注解
 * 校验整个controller或controller的某个方法
 *
 * @author daofeng
 * @version $Id$
 * @since 2021年07月15日 1:54 PM
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SignCheck {
}