package com.xwbing.exception;

/**
 * 创建时间: 2017/11/22 15:08
 * 作者: xiangwb
 * 说明: 工具类异常
 */
public class UtilException extends RuntimeException {
    private static final long serialVersionUID = 3338405359271762532L;

    public UtilException(Throwable cause) {
        super(cause);
    }

    public UtilException(String message) {
        super(message);
    }

    public UtilException(String message, Throwable cause) {
        super(message, cause);
    }
}
