package com.xwbing.util;

/**
 * 作者: xiangwb
 * 说明: 线程变量工具类
 */
public class ThreadLocalUtil {
    private static ThreadLocal<String> token = new ThreadLocal<>();

    private ThreadLocalUtil() {
    }

    public static String getToken() {
        return (String)token.get();
    }

    public static void setToken(String tokenVal) {
        token.set(tokenVal);
    }
}
