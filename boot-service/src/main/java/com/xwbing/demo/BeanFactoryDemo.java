package com.xwbing.demo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月10日 下午1:14
 */
@Slf4j
@Configuration
public class BeanFactoryDemo implements BeanFactoryPostProcessor, BeanPostProcessor {
    private final Set<String> beanNames = new HashSet<>();
    private ConfigurableListableBeanFactory beanFactory;

    @Bean(initMethod = "init", destroyMethod = "shutdown")
    public BeanInitDemo beanInitDemo() {
        BeanInitDemo beanInitDemo = new BeanInitDemo();
        beanInitDemo.setProperty("property");
        return beanInitDemo;
    }

    /**
     * 获取bean的示例或定义等。同时可以修改bean的属性
     *
     * @param beanFactory
     *
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        System.out.println("postProcessBeanFactory...当前BeanFactory中有" + beanFactory.getBeanDefinitionCount() + "个Bean");
        System.out.println(Arrays.asList(beanFactory.getBeanDefinitionNames()));
    }

    // /**
    //  * 如果此方法返回一个非null对象(生成代理对象)，则实例化bean过程将被短路
    //  * 执行postProcessAfterInitialization
    //  *
    //  * @param beanClass
    //  * @param beanName
    //  *
    //  * @return
    //  *
    //  * @throws BeansException
    //  */
    // @Override
    // public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
    //     if (BeanInitDemo.class.getSimpleName().equalsIgnoreCase(beanName)) {
    //         beanNames.add(beanName);
    //         System.out.println("postProcessBeforeInstantiation 实例化bean前 生成代理对象");
    //         return beanClass;
    //     } else {
    //         return null;
    //     }
    // }
    //
    // /**
    //  * 如果返回true，执行postProcessPropertyValues
    //  *
    //  * @param bean
    //  * @param beanName
    //  *
    //  * @return
    //  *
    //  * @throws BeansException
    //  */
    // @Override
    // public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
    //     if (beanNames.contains(beanName)) {
    //         System.out.println("postProcessAfterInstantiation 实例化bean后" + bean);
    //         return true;
    //     } else {
    //         return false;
    //     }
    // }
    //
    // /**
    //  * 可以在该方法内对属性值进行修改
    //  *
    //  * @param pvs
    //  * @param pds
    //  * @param bean
    //  * @param beanName
    //  *
    //  * @return
    //  *
    //  * @throws BeansException
    //  */
    // @Override
    // public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean,
    //         String beanName) throws BeansException {
    //     PropertyValue value = pvs.getPropertyValue("property");
    //     value.setConvertedValue("modifyProperty");
    //     return pvs;
    // }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        // 标记bean
        if (BeanInitDemo.class.getSimpleName().equalsIgnoreCase(beanName)) {
            beanNames.add(beanName);
            System.out.println("postProcessBeforeInitialization 初始化bean前");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //处理bean
        if (beanNames.contains(beanName)) {
            System.out.println("postProcessAfterInitialization 初始化bean后 " + bean);
        }
        return bean;
    }
}
