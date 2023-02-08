package com.xwbing.starter.operatelog.function;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 自定义函数工厂
 *
 * @author daofeng
 * @version $Id$
 * @since 2023年02月06日 5:28 PM
 */
public class CustomFunctionFactory {
    private final Map<String, ICustomFunction> customFunctionMap;

    public CustomFunctionFactory(List<ICustomFunction> functions) {
        customFunctionMap = functions.stream().collect(Collectors.toMap(ICustomFunction::name, Function.identity()));
    }

    public String apply(String name, Object value) {
        ICustomFunction function = customFunctionMap.get(name);
        if (function == null) {
            return "";
        }
        if (value == null) {
            return "";
        }
        return function.apply(value);
    }
}