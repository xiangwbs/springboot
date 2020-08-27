package com.xwbing.config.aliyun.oss;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月26日 下午1:31
 */
@Data
@ConfigurationProperties(prefix = OssProperties.PREFIX)
public class OssProperties {
    public static final String PREFIX = "boot.aliyun.oss";
    private String accessId;
    private String accessSecret;
    /**
     * 访问域名
     */
    private String endpoint;
    /**
     * 存储空间(文件根目录)
     */
    private String bucket;
    /**
     * 地域或者数据中心
     */
    private String regionId;
}