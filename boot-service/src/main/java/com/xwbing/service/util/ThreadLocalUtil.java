package com.xwbing.service.util;

import org.slf4j.MDC;

/**
 * 线程变量工具类
 *
 * @author xiangwb
 */
public class ThreadLocalUtil {
    private static ThreadLocal<String> threadLocal = ThreadLocal.withInitial(String::new);

    private ThreadLocalUtil() {
    }

    public static String getToken() {
        return MDC.get("token");
    }

    public static void setToken(String tokenVal) {
        MDC.put("token", tokenVal);
    }

    public static void clearToken() {
        MDC.remove("token");
    }

    public static String getUser() {
        return MDC.get("user");
    }

    public static void setUser(String tokenVal) {
        MDC.put("user", tokenVal);
    }

    public static void clearUser() {
        MDC.remove("user");
    }
}