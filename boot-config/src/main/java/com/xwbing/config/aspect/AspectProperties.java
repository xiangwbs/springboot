package com.xwbing.config.aspect;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xiangwb
 * 切面属性加载配置类
 */
@ConfigurationProperties(prefix = AspectProperties.PREFIX)
public class AspectProperties {
    public static final String PREFIX = "aspect";
    /**
     * service切入点表达式
     */
    private String servicePointcut;

    public String getServicePointcut() {
        return servicePointcut;
    }

    public void setServicePointcut(String servicePointcut) {
        this.servicePointcut = servicePointcut;
    }
}
