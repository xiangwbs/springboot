package com.xwbing.config.aliyun;

import com.aliyun.openservices.log.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author xiangwb
 * aliyunlog自动配置类
 */
@Configuration
@ConditionalOnProperty(prefix = AliYunLogProperties.PREFIX, name = {"enabled"}, havingValue = "true")
@EnableConfigurationProperties(AliYunLogProperties.class)
public class AliYunLogAutoConfiguration {
    @Resource
    private AliYunLogProperties aliYunLogProperties;

    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client aliYunLogClient() {
        return new Client(aliYunLogProperties.getLog().getEndpoint(),
                aliYunLogProperties.getLog().getAccessId(), aliYunLogProperties.getLog().getAccessKey());
    }

    @Bean
    @ConditionalOnMissingBean(AliYunLog.class)
    public AliYunLog aliYunLog(Client aliYunLogClient) {
        return new AliYunLog(aliYunLogClient, aliYunLogProperties.getLog().getLogStore(), aliYunLogProperties.getLog().getTopic(),
                aliYunLogProperties.getLog().getProject(), aliYunLogProperties.getDingTalk().getWebHook(), aliYunLogProperties.getDingTalk().getSecret());
    }
}
