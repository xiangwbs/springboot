package com.xwbing.service.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

/**
 * 获取本地配置
 *
 * @author daofeng
 * @version $Id$
 * @since 2022年08月21日 10:31 PM
 */
@RequiredArgsConstructor
@Component
public class LocalConfigDemo {
    private final Environment environment;
    /**
     * AutowiredAnnotationBeanPostProcessor.AutowiredFieldElement#inject
     *     ->resolveFieldValue(field, bean, beanName)
     *     	->beanFactory.resolveDependency(desc, beanName, autowiredBeanNames, typeConverter)
     *     		->doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter)
     *                 // 获取到@Value属性上的value
     *     			->Object value = getAutowireCandidateResolver().getSuggestedValue(descriptor)
     *                 // 根据value从env中寻找与之对应的值
     *     			->String strVal = resolveEmbeddedValue((String) value)
     *     ->field.set(bean,value)
     */
    @Value("${server.servlet.context-path}")
    private String path;

    @GetMapping("/config-value")
    public String config() {
        return "path: " + this.path;
    }

    @GetMapping("/conf-env")
    public String confEnv() {
        return "path: " + this.environment.getProperty("server.servlet.context-path");
    }
}