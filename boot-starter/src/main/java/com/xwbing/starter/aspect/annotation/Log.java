package com.xwbing.starter.aspect.annotation;

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
public @interface Log {
    String tag();

    /**
     * SpEL表达式: #p0 | #p0.field | #paramName | #paramName.field
     */
    String content();

    // 是否保存运行结果
    boolean saveResult() default false;
}