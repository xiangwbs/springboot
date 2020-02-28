package com.xwbing.config.exception;

/**
 * @author xiangwb
 * 自定义配置异常
 */
public class ConfigException extends RuntimeException {

    private static final long serialVersionUID = -5078162460549232861L;

    public ConfigException(Throwable cause) {
        super(cause);
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
