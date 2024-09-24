package com.xwbing.service.demo;

import com.xwbing.service.annotation.MyBean;
import lombok.ToString;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 对象初始化先后顺序
 * 父静态>子静态>父构造代码块>父构造方法>子构造代码块>子构造方法
 *
 * @author xiangwb
 */
@MyBean("annotationProperty")
@ToString
public class BeanInitDemo
        implements BeanNameAware, BeanFactoryAware, ApplicationContextAware, InitializingBean, DisposableBean {
    private String property;
    private String beanName;
    private ApplicationContext applicationContext;
    private BeanFactory beanFactory;

    //静态代码块，在该类被加载入JVM时，被执行，且只会被执行一次。不能被继承。
    static {
        System.out.println("1.静态代码块 实例化bean");
    }

    //构造代码块，在类实例化时被调用，每次创建对象都会被调用，执行次序构造代码块>构造函数。
    {
        System.out.println("1.构造代码块 实例化bean");
    }

    // 构造方法，在该类被实例化的时候被执行。每个类都有隐式的空构造函数，如果定义了非空构造函数，需要自定义空构造函数
    public BeanInitDemo() {
        System.out.println("1.构造器 实例化bean");
    }

    public BeanInitDemo(String property) {
        this.property = property;
        System.out.println("1.构造器 实例化bean property:" + property);
    }

    public void setProperty(String property) {
        System.out.println("2.setProperty:" + property + " bean属性注入");
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
        System.out.println("3.BeanNameAware.setBeanName " + beanName + " 检查Aware相关接口并设置相关依赖");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        System.out.println("3.BeanFactoryAware.setBeanFactory 检查Aware相关接口并设置相关依赖");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        System.out.println("3.ApplicationContextAware.setApplicationContext 检查Aware相关接口并设置相关依赖");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("4.@PostConstruct 初始化bean InitDestroyAnnotationBeanPostProcessor.postProcessBeforeInitialization");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("5.InitializingBean.afterPropertiesSet 初始化bean");
    }

    public void init() {
        System.out.println("5.@Bean.initMethod 初始化bean");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("6.@PreDestroy 销毁bean InitDestroyAnnotationBeanPostProcessor.postProcessBeforeDestruction");
    }

    @Override
    public void destroy() {
        System.out.println("7.DisposableBean.destroy 销毁bean");
    }

    public void shutdown() {
        System.out.println("7.@Bean.destroyMethod 销毁bean");
    }
}