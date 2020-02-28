package com.xwbing.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者: xiangwb
 * 说明: 公共数据类
 */
public class CommonDataUtil {
    private static  Map<String, Object> token=new HashMap<>();

    private CommonDataUtil() {
    }

    public static Object getToken(String key) {
        return token.get(key);
    }

    public static void setToken(String key, Object value) {
        token.put(key, value);
    }

    public static void clearMap() {
        token.clear();
    }
}
