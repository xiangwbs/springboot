package com.xwbing.util;

/**
 * 线程变量工具类
 *
 * @author xiangwb
 */
public class ThreadLocalUtil {
    private static ThreadLocal<String> token = new ThreadLocal<>();

    private ThreadLocalUtil() {
    }

    public static String getToken() {
        return token.get();
    }

    public static void setToken(String tokenVal) {
        token.set(tokenVal);
    }
}
