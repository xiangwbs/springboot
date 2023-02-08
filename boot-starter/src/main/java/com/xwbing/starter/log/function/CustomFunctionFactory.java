package com.xwbing.starter.log.function;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

/**
 * 自定义函数工厂
 */
public class CustomFunctionFactory {
    private static final Map<String, ICustomFunction> CUSTOM_FUNCTION_MAP = new HashMap<>();

    public CustomFunctionFactory(List<ICustomFunction> customFunctions) {
        if (CollectionUtils.isNotEmpty(customFunctions)) {
            customFunctions.forEach(customFunction -> CUSTOM_FUNCTION_MAP.put(customFunction.functionName(), customFunction));
        }
    }

    public String apply(String functionName, Object value) {
        ICustomFunction function = getFunction(functionName);
        if (function == null) {
            return "";
        }
        if (value == null) {
            return "";
        }
        return function.apply(value);
    }

    private ICustomFunction getFunction(String functionName) {
        return CUSTOM_FUNCTION_MAP.get(functionName);
    }
}