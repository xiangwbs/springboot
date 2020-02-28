package com.xwbing.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取请求头数据工具类
 */
public class HeaderUtil {
    /**
     * 获取token
     *
     * @param req
     * @return
     */
    public static String getToken(HttpServletRequest req) {
        return req.getHeader("token");
    }

    /**
     * 获取appid
     *
     * @param req
     * @return
     */
    public static String getAppId(HttpServletRequest req) {
        return req.getHeader("appId");
    }

    /**
     * 获取设备id
     *
     * @param req
     * @return
     */
    public static String getDeviceId(HttpServletRequest req) {
        return req.getHeader("deviceId");
    }

    /**
     * 获取用户id
     *
     * @param req
     * @return
     */
    public static String getUserId(HttpServletRequest req) {
        return req.getHeader("userId");
    }

    /**
     * 获取场景
     *
     * @param req
     * @return
     */
    public static String getSceneType(HttpServletRequest req) {
        return req.getHeader("sceneType");
    }

    /**
     * 获取版本号
     *
     * @param req
     * @return
     */
    public static int getVersionCode(HttpServletRequest req) {
        String appversion = req.getHeader("appVersion");
        if (StringUtils.isNotEmpty(appversion)) {
            appversion = appversion.replaceAll("\\.", "");
            return Integer.parseInt(appversion);
        }
        return 0;
    }

    /**
     * 获取请求校验版本
     *
     * @param req
     * @return
     */
    public static int getVersion(HttpServletRequest req) {
        String version = req.getHeader("version");
        if (StringUtils.isEmpty(version)) {
            return 1;
        }
        return Integer.parseInt(version);
    }
}
