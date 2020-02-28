package com.xwbing.exception;

/**
 * 说明: 支付异常
 * 创建时间: 2017/5/10 17:31
 * 作者:  xiangwb
 */

public class PayException extends RuntimeException {
    private static final long serialVersionUID = 584325238279858855L;

    public PayException(Throwable cause) {
        super(cause);
    }

    public PayException(String message) {
        super(message);
    }

    public PayException(String message, Throwable cause) {
        super(message, cause);
    }
}
