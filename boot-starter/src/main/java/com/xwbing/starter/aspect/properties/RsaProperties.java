package com.xwbing.starter.aspect.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年07月15日 1:54 PM
 */
@Data
@ConfigurationProperties(prefix = RsaProperties.PREFIX)
public class RsaProperties {
    public static final String PREFIX = "boot.rsa";
    /**
     * 私钥(Base64编码)
     */
    private String privateKeyBase64;
    /**
     * 公钥(Base64编码)
     */
    private String publicKeyBase64;
}
