package com.xwbing.starter.alipay;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年07月24日 下午9:57
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(AliPayProperties.class)
public class AliPayAutoConfiguration {
    private final AliPayProperties properties;

    public AliPayAutoConfiguration(AliPayProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnExpression("!'${alipay.alipay-root-cert-path:}'.empty")
    public AlipayClient aliPayCertClient() {
        try {
            CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
            certAlipayRequest.setServerUrl(properties.getServerUrl());
            certAlipayRequest.setAppId(properties.getAppId());
            certAlipayRequest.setPrivateKey(properties.getAppPrivateKey());
            certAlipayRequest.setCertPath(properties.getAppCertPublicKeyPath());
            certAlipayRequest.setAlipayPublicCertPath(properties.getAliPayPublicCertPath());
            certAlipayRequest.setRootCertPath(properties.getAliPayRootCertPath());
            certAlipayRequest.setFormat(properties.getFormat());
            certAlipayRequest.setCharset(properties.getCharset());
            certAlipayRequest.setSignType(properties.getSignType());
            return new DefaultAlipayClient(certAlipayRequest);
        } catch (Exception e) {
            log.error("initAliPayCertClient error", e);
            return null;
        }
    }

    @Bean
    @ConditionalOnBean(AlipayClient.class)
    public AliPayHelper aliPayService(AlipayClient alipayClient, AliPayProperties properties) {
        return new AliPayHelper(alipayClient, properties);
    }
}