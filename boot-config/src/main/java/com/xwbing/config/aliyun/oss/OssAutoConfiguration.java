package com.xwbing.config.aliyun.oss;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月26日 下午1:31
 */
@Configuration
@ConditionalOnProperty(prefix = OssProperties.PREFIX, name = { "enabled" }, havingValue = "true")
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {
    private final OssProperties ossProperties;

    public OssAutoConfiguration(OssProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean(OSSClient.class)
    public OSSClient ossClient() {
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(ossProperties.getAccessId(),
                ossProperties.getAccessKey());
        ClientConfiguration config = new ClientConfiguration();
        return new OSSClient(ossProperties.getEndpoint(), credentialsProvider, config);
    }

    @Bean
    @ConditionalOnMissingBean(DefaultAcsClient.class)
    public DefaultAcsClient defaultAcsClient() {
        return new DefaultAcsClient(DefaultProfile
                .getProfile(ossProperties.getRegionId(), ossProperties.getAccessId(), ossProperties.getAccessKey()));
    }

    @Bean
    @ConditionalOnBean(OSSClient.class)
    public OssService ossService(OSSClient ossClient) {
        return new OssService(ossClient, ossProperties);
    }
}