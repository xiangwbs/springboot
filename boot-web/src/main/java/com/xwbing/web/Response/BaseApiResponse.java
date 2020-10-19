package com.xwbing.web.Response;

/**
 * @author daofeng
 * @version $Id$
 * @since 2018/4/22 14:07
 */
class BaseApiResponse {

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorTrace() {
        return errorTrace;
    }

    public void setErrorTrace(String errorTrace) {
        this.errorTrace = errorTrace;
    }

    public BaseApiResponse() {
    }

    public BaseApiResponse(String code, String error) {
        this.code = code;
        this.error = error;
    }

    public BaseApiResponse(String code, String error, String errorTrace) {
        this.code = code;
        this.error = error;
        this.errorTrace = errorTrace;
    }

    public boolean success() {
        return ApiResponseUtil.DEFAULT_SUCCESS_CODE.equalsIgnoreCase(code);
    }

}
