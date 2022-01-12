package com.xwbing.starter.wx.mp;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年10月05日 9:29 AM
 */
@Data
@ConfigurationProperties(prefix = WxMpProperties.PREFIX)
public class WxMpProperties {
    public static final String PREFIX = "boot.wx.mp";
    /**
     * 设置微信公众号的appid.
     */
    private String appId;
    /**
     * 设置微信公众号的app secret.
     */
    private String secret;
    /**
     * 设置微信公众号的token
     */
    private String token;
    /**
     * 设置微信公众号的EncodingAESKey
     */
    private String aesKey;
}