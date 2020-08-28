package com.xwbing.starter.alipay;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年07月24日 下午9:33
 */
@Data
@ConfigurationProperties(prefix = AliPayProperties.PREFIX)
public class AliPayProperties {
    public static final String PREFIX = "alipay";
    /**
     * 支付宝网关
     */
    private String serverUrl;
    /**
     * 支付宝分配给开发者的应用ID
     */
    private String appId;
    /**
     * 应用公钥证书
     */
    private String appCertPublicKeyPath;
    /**
     * 支付宝公钥证书
     */
    private String aliPayPublicCertPath;
    /**
     * 支付宝根证书
     */
    private String aliPayRootCertPath;
    /**
     * 应用私钥
     */
    private String appPrivateKey;
}
