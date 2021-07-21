package com.xwbing.starter.signature;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.hutool.crypto.asymmetric.RSA;
import lombok.extern.slf4j.Slf4j;

/**
 * 验签切面自动配置类
 *
 * @author daofeng
 * @version $Id$
 * @since 2021年07月15日 1:54 PM
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RsaProperties.class)
public class SignCheckAutoConfiguration {
    private final RsaProperties rsaProperties;

    public SignCheckAutoConfiguration(RsaProperties rsaProperties) {
        this.rsaProperties = rsaProperties;
    }

    @Bean
    public SignCheckAspect signCheckAspect() {
        RSA rsa = new RSA(rsaProperties.getPrivateKeyBase64(), rsaProperties.getPublicKeyBase64());
        return new SignCheckAspect(rsa);
    }
}