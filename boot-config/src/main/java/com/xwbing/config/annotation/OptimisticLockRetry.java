package com.xwbing.config.annotation;

import java.lang.annotation.*;

/**
 * @author xiangwb
 * 乐观锁重试注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface OptimisticLockRetry {
    /**
     * 最大重试次数
     * 阿里巴巴java开发手册建议乐观锁重试次数不得小于3次
     *
     * @return try num
     */
    int value() default 3;
}
