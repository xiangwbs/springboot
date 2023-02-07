package com.xwbing.starter.util;

import org.slf4j.MDC;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年02月07日 10:38 AM
 */
public class UserContext {
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