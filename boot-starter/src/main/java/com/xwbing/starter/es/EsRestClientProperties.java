package com.xwbing.starter.es;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author daofeng
 * @version $Id$
 * @since 2022年07月05日 2:56 PM
 */
@Data
@ConfigurationProperties(prefix = EsRestClientProperties.PREFIX)
public class EsRestClientProperties {
    public static final String PREFIX = "es";
    /** 字符串list配置链接节点，格式ip:port形式 */
    private List<String> hosts;
    /** 连接超时，默认：5000ms */
    private int connectTimeout = 20000;
    /** 接口超时，默认：6000ms */
    private int socketTimeout = 20000;
    /** 连接最大重试时间，默认：60000ms */
    private int maxRetryTimeoutMillis = 20000;
    /** 登录用户名 */
    private String username = "";
    /** 登录用户密码 */
    private String password = "";
}