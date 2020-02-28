package com.xwbing.domain.entity.pay.alipay;

/**
 * 说明: 支付宝返回状态码
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:37
 * 作者:  xiangwb
 */

public class AliPayConstant {
    /**
     * 接口调用成功
     */
    public static String success_code = "10000";
    /**
     * 服务不可用
     */
    public static String service_unavailable = "20000";
    /**
     * 授权权限不足
     */
    public static String limited_authority = "20001";
    /**
     * 缺少必选参数
     */
    public static String miss_argument = "40001";
    /**
     * 非法的参数
     */
    public static String invalid_argument = "40002";
    /**
     * 业务处理失败
     */
    public static String business_handling_failure = "40004";
    /**
     * 权限不足
     */
    public static String insufficient_permissions = "40006";
}
