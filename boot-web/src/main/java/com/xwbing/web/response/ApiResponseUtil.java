package com.xwbing.web.response;

/**
 * @author daofeng
 * @version $Id:
 * @since 2018年06月14日 下午5:04
 */
public class ApiResponseUtil {
    static final String DEFAULT_SUCCESS_CODE = "200";
    private static final String DEFAULT_FAIL_CODE = "400";

    /**
     * 返回成功结果
     *
     * @param obj
     * @param <T>
     *
     * @return
     */
    public static <T> ApiResponse<T> success(T obj) {
        return new ApiResponse<>(DEFAULT_SUCCESS_CODE, "", obj);
    }

    /**
     * 返回成功结果，data为null
     *
     * @return
     */
    public static ApiResponse success() {
        return success(null);
    }

    /**
     * 返回失败结果,code默认为HttpStatus.BAD_REQUEST
     *
     * @param error
     *
     * @return
     */
    public static ApiResponse fail(String error) {
        return fail(DEFAULT_FAIL_CODE, error);
    }

    /**
     * 返回失败结果
     *
     * @param code
     * @param error
     *
     * @return
     */
    public static ApiResponse fail(String code, String error) {
        return new ApiResponse<>(code, error, null);
    }

    /**
     * 返回失败结果，code为int类型
     *
     * @param code
     * @param error
     *
     * @return
     */
    public static ApiResponse fail(int code, String error) {
        return fail(String.valueOf(code), error);
    }

    /**
     * 返回失败结果
     *
     * @param code
     * @param error
     * @param errorTrace
     *
     * @return
     */
    public static ApiResponse fail(String code, String error, String errorTrace) {
        return new ApiResponse<>(code, error, errorTrace, null);
    }
}
