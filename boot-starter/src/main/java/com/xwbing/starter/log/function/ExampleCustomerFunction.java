package com.xwbing.starter.log.function;

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
    public String functionName() {
        return "exampleFunction";
    }

    @Override
    public String apply(Object value) {
        return value.toString() + "exampleFunction";
    }
}