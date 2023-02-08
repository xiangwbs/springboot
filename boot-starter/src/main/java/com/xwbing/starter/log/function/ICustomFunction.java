package com.xwbing.starter.log.function;

/**
 * 自定义函数接口
 *
 * @author daofeng
 * @version $Id$
 * @since 2023年02月06日 5:28 PM
 */
public interface ICustomFunction {
    /**
     * 自定义函数名
     */
    String name();

    /**
     * 自定义函数
     *
     * @param param 参数
     *
     * @return 执行结果
     */
    String apply(Object param);
}