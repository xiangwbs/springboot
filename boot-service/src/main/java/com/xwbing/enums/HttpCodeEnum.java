package com.xwbing.enums;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年06月19日 下午2:54
 */
public enum HttpCodeEnum {
    /** */
    OK("成功", 200),
    UNAUTHORIZED("请求要求身份验证", 401),
    FORBIDDEN("服务器拒绝请求", 403),
    NOT_FOUND("服务器找不到请求的网页", 404),
    ERROR("服务器遇到错误,无法完成请求", 500),
    SERVICE_UNAVAILABLE("服务器暂不可用", 503),
    GATEWAY_TIME_OUT("网关超时", 504),
    ;
    private String name;
    private int value;

    HttpCodeEnum(String name, int value) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
