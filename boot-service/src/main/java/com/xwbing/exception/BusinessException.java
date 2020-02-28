package com.xwbing.exception;

/**
 * 说明:  自定义业务异常
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 7493711492820795133L;

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

}
