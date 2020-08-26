package com.xwbing.service.demo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import com.xwbing.service.annotation.MyBean;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月10日 下午1:14
 */
@Slf4j
@Configuration
public class BeanFactoryDemo implements BeanFactoryPostProcessor, BeanPostProcessor, Ordered {
    private final Set<String> beanNames = new HashSet<>();
    private final Map<String, MyBean> annotationMap = new HashMap<>();

    private DefaultListableBeanFactory beanFactory;

    @Bean(name = "defaultBeanInitDemo", initMethod = "init", destroyMethod = "shutdown")
    public BeanInitDemo beanInitDemo() {
        BeanInitDemo beanInitDemo = new BeanInitDemo();
        beanInitDemo.setProperty("property");
        return beanInitDemo;
    }

    /**
     * 获取bean的示例或定义等。同时可以修改bean的属性
     * 与bean definitions打交道，但是千万不要进行bean实例化
     *
     * @param beanFactory
     *
     * @throws BeansException
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory)beanFactory;
        System.out.println("postProcessBeanFactory...");
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            MyBean annotation = beanFactory.findAnnotationOnBean(beanName, MyBean.class);
            if (annotation != null) {
                AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition)beanFactory
                        .getBeanDefinition(beanName);
                AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                MultiValueMap<String, Object> allAnnotationAttributes = annotationMetadata
                        .getAllAnnotationAttributes(MyBean.class.getCanonicalName());
                annotationMap.put(beanName, annotation);
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder
                        .rootBeanDefinition(BeanInitDemo.class).addPropertyValue("property", "definitionProperty")
                        .addPropertyValue("beanName", "definitionBeanInitDemo")
                        .addPropertyValue("beanFactory", beanFactory).setInitMethodName("init")
                        .setDestroyMethodName("shutdown");
                this.beanFactory
                        .registerBeanDefinition("definitionBeanInitDemo", beanDefinitionBuilder.getBeanDefinition());
            }
        }
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
        MyBean annotation = bean.getClass().getAnnotation(MyBean.class);
        if (annotation != null) {
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

    /**
     * Find a {@link BeanDefinition} in the {@link BeanFactory} or it's parents.
     *
     * @param beanFactory
     * @param beanName
     *
     * @return
     */
    private BeanDefinition findBeanDefinition(ConfigurableListableBeanFactory beanFactory, String beanName) {
        if (beanFactory.containsLocalBean(beanName)) {
            return beanFactory.getBeanDefinition(beanName);
        }
        BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
        if (ConfigurableListableBeanFactory.class.isInstance(parentBeanFactory)) {
            return findBeanDefinition((ConfigurableListableBeanFactory)parentBeanFactory, beanName);
        }
        throw new RuntimeException(String.format("Bean with name '%s' can no longer be found.", beanName));
    }

    /**
     * 根据beanName获取Class
     *
     * @param beanName
     *
     * @return
     */
    private Class<?> getClass(String beanName) {
        BeanDefinition beanDefinition = this.findBeanDefinition(beanFactory, beanName);
        String className = beanDefinition.getBeanClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}