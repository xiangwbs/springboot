package com.xwbing.config.aspect;

import org.springframework.aop.aspectj.AspectJExpressionPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author xiangwb
 * 切面自动配置类
 */
@Configuration
@EnableConfigurationProperties(AspectProperties.class)
public class AspectAutoConfiguration {
    @Resource
    private AspectProperties aspectProperties;

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
    public LockAspect lockAspect() {
        return new LockAspect();
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
    @ConditionalOnMissingBean(IdempotentAspect.class)
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }
}
