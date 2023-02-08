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
        return threadLocal.get();
    }

    public static void setToken(String tokenVal) {
        threadLocal.set(tokenVal);
    }

    public static void clearToken() {
        threadLocal.remove();
    }

    public static String getTraceId() {
        return MDC.get("traceId");
    }

    public static void setTraceId(String tokenVal) {
        MDC.put("traceId", tokenVal);
    }

    public static void clearTraceId() {
        MDC.remove("traceId");
    }
}