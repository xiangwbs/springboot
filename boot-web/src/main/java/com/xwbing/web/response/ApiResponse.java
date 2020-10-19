package com.xwbing.web.response;

import lombok.Data;

/**
 * @author daofneg
 * @version $Id$
 * @since 2018/4/22 14:07
 */
@Data
public class ApiResponse<T> {
    /**
     * 返回数据
     */
    private T data;
    /**
     * 返回码
     */
    private String code;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 详细错误信息，调试用
     */
    private String errorTrace;

    public ApiResponse() {
    }

    public ApiResponse(T data) {
        this.data = data;
    }

    public ApiResponse(String code, String error, T data) {
        this.code = code;
        this.error = error;
        this.data = data;
    }

    public ApiResponse(String code, String error, String errorTrace, T data) {
        this.code = code;
        this.error = error;
        this.errorTrace = errorTrace;
        this.data = data;
    }

    public boolean success() {
        return ApiResponseUtil.DEFAULT_SUCCESS_CODE.equalsIgnoreCase(code);
    }
}
