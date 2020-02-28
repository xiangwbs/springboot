package com.xwbing.util;

import com.xwbing.constant.Base;
import org.apache.commons.lang3.StringUtils;

/**
 * @author xiangwb
 * @date 20/1/18 20:41
 * 环境工具类
 */
public class EnvUtil {
    /**
     * 获取运行环境
     */
    public static String getEnv() {
        String env = System.getenv("env");
        if (StringUtils.isNotEmpty(env)) {
            return env;
        }
        return Base.ENV_TEST;
    }

    public static String getZone() {
        String zone = System.getenv("zone");
        if (StringUtils.isNotEmpty(zone)) {
            return zone;
        }
        return "ZG001";
    }

    public static String getHost() {
        String hostName = System.getenv("hostName");
        if (StringUtils.isNotEmpty(hostName)) {
            return hostName;
        }
        return "xwb-dev";
    }

    /**
     * 是否开发测试环境
     *
     * @return
     */
    public static boolean isTestEnv() {
        String env = getEnv();
        if (StringUtils.isEmpty(env)
                || StringUtils.equals(env, Base.ENV_DEV)
                || StringUtils.equals(env, Base.ENV_TEST)) {
            return true;
        }
        return false;
    }
}
