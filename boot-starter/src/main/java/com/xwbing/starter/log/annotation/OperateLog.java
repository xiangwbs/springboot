package com.xwbing.starter.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.xwbing.starter.log.function.ExampleCustomerFunction;
import com.xwbing.starter.log.function.ICustomFunction;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年02月06日 5:28 PM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperateLog {
    /**
     * 标签
     */
    String tag();

    /**
     * SpEL语法: #p0 | #p0.field | #paramName | #paramName.field
     * 自定义语法: {functionName{SpEL}}|{{SpEL}} 函数需继承{@link ICustomFunction} 示例查看{@link ExampleCustomerFunction}
     * 特殊参数:操作人{{_operator}} 执行结果:{{#_result}} 错误信息{{_errMsg}}
     *
     * 请使用纯SpEL语法或自定义语法
     */
    String content();
}