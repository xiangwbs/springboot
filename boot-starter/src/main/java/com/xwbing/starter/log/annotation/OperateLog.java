package com.xwbing.starter.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年02月06日 5:28 PM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperateLog {
    String tag();

    /**
     * SpEL表达式: #p0 | #p0.field | #paramName | paramName.field
     * 语法:{function{SpEL}}|{{SpEL}}|SpEL
     */
    String content();
}