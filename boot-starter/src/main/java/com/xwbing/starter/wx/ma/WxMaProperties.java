package com.xwbing.starter.wx.ma;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Data
@ConfigurationProperties(prefix = WxMaProperties.PREFIX)
public class WxMaProperties {
    public static final String PREFIX = "boot.wx.ma";
    /**
     * 设置微信小程序的appid
     */
    private String appId;
    /**
     * 设置微信小程序的Secret
     */
    private String secret;
    /**
     * 设置微信小程序消息服务器配置的token
     */
    private String token;
    /**
     * 设置微信小程序消息服务器配置的EncodingAESKey
     */
    private String aesKey;
    /**
     * 消息格式，XML或者JSON
     */
    private String msgDataFormat;
}
