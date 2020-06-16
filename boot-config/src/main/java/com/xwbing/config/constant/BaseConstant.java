package com.xwbing.config.constant;

public class BaseConstant {
    /**
     * 业务
     */
    //正式环境
    public static final int BUSINESS_LEASE_PROD = 1;
    //预发环境
    public static final int BUSINESS_LEASE_PRE = 2;
    //测试环境
    public static final int BUSINESS_LEASE_TEST = 3;
    //开发环境
    public static final int BUSINESS_LEASE_DEV = 4;
    /**
     * 应用环境
     */
    //开发环境
    public static final String ENV_DEV = "dev";
    //测试环境
    public static final String ENV_TEST = "test";
    //预发环境
    public static final String ENV_PRE = "pre";
    //正式环境
    public static final String ENV_PROD = "prod";

    public static final String HOST = System.getenv("hostName");
}
