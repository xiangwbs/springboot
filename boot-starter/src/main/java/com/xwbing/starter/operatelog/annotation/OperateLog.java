package com.xwbing.starter.operatelog.annotation;

import com.xwbing.starter.operatelog.function.ExampleCustomerFunction;
import com.xwbing.starter.operatelog.function.ICustomFunction;

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
    /**
     * 标签
     */
    String tag();

    /**
     * SpEL语法: #p0 | #p0.field | #p0.getField() | #paramName | #paramName.field | #paramName.getField()
     * 自定义语法: {functionName{SpEL}}|{{SpEL}} 函数需继承{@link ICustomFunction} 示例查看{@link ExampleCustomerFunction}
     * 特殊参数:操作人{{_operator}} 执行结果:{{#_result}} 错误信息{{_errMsg}}
     *
     * 请使用纯SpEL语法或自定义语法
     */
    String content();
}