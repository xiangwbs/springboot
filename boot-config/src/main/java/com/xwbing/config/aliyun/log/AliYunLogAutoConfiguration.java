package com.xwbing.config.aliyun.log;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyun.openservices.log.Client;

/**
 * aliyunlog自动配置类
 *
 * @author xiangwb
 */
@Configuration
@ConditionalOnProperty(prefix = AliYunLogProperties.PREFIX, name = { "enabled" }, havingValue = "true")
@EnableConfigurationProperties(AliYunLogProperties.class)
public class AliYunLogAutoConfiguration {
    private final AliYunLogProperties aliYunLogProperties;

    public AliYunLogAutoConfiguration(AliYunLogProperties aliYunLogProperties) {
        this.aliYunLogProperties = aliYunLogProperties;
    }

    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client aliYunLogClient() {
        return new Client(aliYunLogProperties.getEndpoint(), aliYunLogProperties.getAccessId(),
                aliYunLogProperties.getAccessSecret());
    }

    @Bean
    @ConditionalOnMissingBean(AliYunLog.class)
    public AliYunLog aliYunLog(Client aliYunLogClient) {
        return new AliYunLog(aliYunLogClient, aliYunLogProperties.getLogStore(), aliYunLogProperties.getTopic(),
                aliYunLogProperties.getProject());
    }
}