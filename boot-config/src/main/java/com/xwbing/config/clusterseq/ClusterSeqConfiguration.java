package com.xwbing.config.clusterseq;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xwbing.config.constant.BaseConstant;
import com.xwbing.config.redis.RedisService;

/**
 * @author daofeng
 * @version $
 * @since 2020年01月03日 11:50
 */
@Configuration
@ConditionalOnClass(RedisService.class)
public class ClusterSeqConfiguration {
    @Value("${spring.profiles.active:test}")
    private String env;
    @Resource
    private RedisService redisService;

    @Bean
    @ConditionalOnMissingBean(ClusterSeqGenerator.class)
    public ClusterSeqGenerator clusterSeqGenerator() {
        int envType;
        if (StringUtils.equals(env, BaseConstant.ENV_DEV)) {
            envType = BaseConstant.BUSINESS_LEASE_DEV;
        } else if (StringUtils.equals(env, BaseConstant.ENV_PRE)) {
            envType = BaseConstant.BUSINESS_LEASE_PRE;
        } else if (StringUtils.equals(env, BaseConstant.ENV_PROD)) {
            envType = BaseConstant.BUSINESS_LEASE_PROD;
        } else {
            envType = BaseConstant.BUSINESS_LEASE_TEST;
        }
        return new ClusterSeqGenerator(redisService, envType);
    }
}