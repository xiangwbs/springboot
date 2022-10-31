package com.xwbing.service.demo.ratelimit.Sentinel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;

@Configuration
public class SentinelConfiguration {
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
}
