package com.xwbing.starter.log.function;

/**
 * 自定义函数接口
 *
 * @author qfwang666@163.com
 */
public interface ICustomFunction {
    /**
     * 自定义函数名
     *
     * @return 自定义函数名
     */
    String functionName();

    /**
     * 自定义函数
     *
     * @param param 参数
     *
     * @return 执行结果
     */
    String apply(Object param);
}