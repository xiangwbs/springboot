package com.xwbing.starter.yunxin;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年07月24日 下午9:33
 */
@Data
@ConfigurationProperties(prefix = YunXinProperties.PREFIX)
public class YunXinProperties {
    public static final String PREFIX = "boot.yunxin";
    private String appKey;
    private String appSecret;
}