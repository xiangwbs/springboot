package com.xwbing.starter.operatelog.function;

/**
 * 后置自定义函数示例
 *
 * @author daofeng
 * @version $Id$
 * @since 2023年02月06日 5:28 PM
 */
@Deprecated
public class ExampleCustomerFunction implements ICustomFunction {
    @Override
    public String name() {
        return "exampleFunction";
    }

    @Override
    public String apply(Object value) {
        return "我不是" + value;
    }
}