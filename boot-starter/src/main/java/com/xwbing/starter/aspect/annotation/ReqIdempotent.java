package com.xwbing.starter.aspect.annotation;

import java.lang.annotation.*;

import com.xwbing.starter.aspect.enums.IdempotentParamTypeEnum;

/**
 * @author xiangwb
 *         解决接口幂等性 支持网络延迟和表单重复提交
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ReqIdempotent {

    IdempotentParamTypeEnum paramType() default IdempotentParamTypeEnum.HEADER;
}