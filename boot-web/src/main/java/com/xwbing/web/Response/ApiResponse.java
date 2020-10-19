package com.xwbing.web.Response;

/**
 * @author daofneg
 * @version $Id$
 * @since 2018/4/22 14:07
 */
public class ApiResponse<T> extends BaseApiResponse {

    /**
     * 返回数据
     */
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiResponse() {
    }

    public ApiResponse(T data) {
        this.data = data;
    }

    public ApiResponse(String code, String error, T data) {
        super(code, error);
        this.data = data;
    }

    public ApiResponse(String code, String error, String errorTrace, T data) {
        super(code, error, errorTrace);
        this.data = data;
    }

    public boolean isSuccess() {
        return ApiResponseUtil.DEFAULT_SUCCESS_CODE.equals(getCode());
    }
}
