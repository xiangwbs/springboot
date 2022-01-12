package com.xwbing.starter.wx.open.app;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年10月05日 9:29 AM
 */
@Data
@ConfigurationProperties(prefix = WxOpenAppProperties.PREFIX)
public class WxOpenAppProperties {
    public static final String PREFIX = "boot.wx.open.app";
    private List<Config> configs;

    @Data
    public static class Config {
        /**
         * 设置移动/网页应用的appid
         */
        private String appId;
        /**
         * 设置移动/网页应用的Secret
         */
        private String secret;
    }
}