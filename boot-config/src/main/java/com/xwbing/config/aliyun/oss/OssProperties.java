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
     * OSS 访问域名
     */
    private String endpoint;
    /**
     * 存储空间(文件根目录)
     */
    private String bucket;
    /**
     * bucket所在的区域
     */
    private String region;
    /**
     * bucket所在的区域id
     */
    private String regionId;
    /**
     * 角色
     */
    private String stsRoleArn;
    /**
     * 别名
     */
    private String stsRoleSessionName;
}