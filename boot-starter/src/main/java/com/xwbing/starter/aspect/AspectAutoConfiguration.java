package com.xwbing.starter.aspect;

import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xwbing.starter.aspect.properties.AspectProperties;
import com.xwbing.starter.aspect.properties.RsaProperties;
import com.xwbing.starter.redis.RedisService;

import cn.hutool.crypto.asymmetric.RSA;

/**
 * 切面自动配置类
 *
 * @author daofeng
 * @version $Id$
 * @since 2021年07月15日 1:54 PM
 */
@Configuration
@EnableConfigurationProperties({ AspectProperties.class, RsaProperties.class })
public class AspectAutoConfiguration {
    private final AspectProperties aspectProperties;
    private final RsaProperties rsaProperties;

    public AspectAutoConfiguration(AspectProperties aspectProperties, RsaProperties rsaProperties) {
        this.aspectProperties = aspectProperties;
        this.rsaProperties = rsaProperties;
    }

    /**
     * service异常日志切面
     *
     * @return
     */
    @Bean
    @ConditionalOnExpression("!'${aspect.service-pointcut:}'.empty")
    public AspectJExpressionPointcutAdvisor throwsAdvisor() {
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        //切入点表达式
        advisor.setExpression(aspectProperties.getServicePointcut());
        //通知
        advisor.setAdvice(new ExceptionLogAdvice());
        return advisor;
    }

    /**
     * 基于redis分布式锁
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(LockAspect.class)
    public LockAspect lockAspect(RedisService redisService) {
        return new LockAspect(redisService);
    }

    /**
     * 乐观锁重试机制
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(OptimisticLockRetryAspect.class)
    public OptimisticLockRetryAspect optimisticLockRetryAspect() {
        return new OptimisticLockRetryAspect();
    }

    /**
     * 令牌桶限流
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(FlowLimiterAspect.class)
    public FlowLimiterAspect flowLimiterAspect() {
        return new FlowLimiterAspect();
    }

    /**
     * 幂等校验
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ReqIdempotentAspect.class)
    public ReqIdempotentAspect idempotentAspect() {
        return new ReqIdempotentAspect();
    }

    /**
     * 频率校验
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ReqLimitAspect.class)
    public ReqLimitAspect limitAspect(RedisService redisService) {
        return new ReqLimitAspect(redisService);
    }

    /**
     * 验签
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(ReqVerifyAspect.class)
    public ReqVerifyAspect reqVerifyAspect() {
        RSA rsa = new RSA(rsaProperties.getPrivateKeyBase64(), rsaProperties.getPublicKeyBase64());
        return new ReqVerifyAspect(rsa);
    }
}
