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
public @interface ReqLimit {
    /**
     * 超时时间(限制时间) 秒
     */
    int timeout() default 10;

    // /**
    //  * 时间单位
    //  */
    // TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 限制标识
     * SpEL表达式:#p0|#p0.field|#paramName|#paramName.field
     *
     * @return
     */
    String value();

    /**
     * 限制描述
     *
     * @return
     */
    String remark() default "您的操作太快, 请稍后再试";
}
