package com.xwbing.demo;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月10日 下午1:14
 */
@Configuration
public class BeanFactoryDemo implements BeanPostProcessor {

    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public BeanInitDemo beanInitDemo() {
        BeanInitDemo beanInitDemo = new BeanInitDemo();
        beanInitDemo.setProperty("property");
        return beanInitDemo;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String s) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String s) throws BeansException {
        return bean;
    }
}
