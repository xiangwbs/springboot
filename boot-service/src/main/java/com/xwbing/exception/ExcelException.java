package com.xwbing.exception;

/**
 * excel处理异常
 */
public class ExcelException extends RuntimeException {
    private static final long serialVersionUID = 7493711492820795133L;

    public ExcelException(Throwable cause) {
        super(cause);
    }

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }

}
