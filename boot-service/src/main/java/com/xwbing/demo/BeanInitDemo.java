package com.xwbing.demo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author xiangwb
 *         对象初始化先后顺序
 *         父静态>子静态>父构造代码块>父构造方法>子构造代码块>子构造方法
 *         <p>
 *         InstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation>静态代码块>构造代码块>构造函数>InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation>
 *         setXXX|autowire>BeanPostProcessor.postProcessBeforeInitialization>@PostConstruct>afterPropertiesSet>init-method
 *         >BeanPostProcessor.postProcessAfterInitialization>DisposableBean.destroy>destroy-method
 */
public class BeanInitDemo implements BeanNameAware, BeanFactoryAware, InitializingBean, DisposableBean {
    private String property;

    //静态代码块，在该类被加载入JVM时，被执行，且只会被执行一次。不能被继承。
    static {
        System.out.println("1.静态代码块 实例化bean");
    }

    //构造代码块，在类实例化时被调用，每次创建对象都会被调用，执行次序构造代码块>构造函数。
    {
        System.out.println("2.构造代码块 实例化bean");
    }

    // 构造方法，在该类被实例化的时候被执行。每个类都有隐式的空构造函数，如果定义了非空构造函数，需要自定义空构造函数
    public BeanInitDemo() {
        System.out.println("3.构造器 实例化bean");

    }

    public void setProperty(String property) {
        System.out.println("4.setProperty 属性赋值");
        this.property = property;
    }

    @Override
    public void setBeanName(String s) {
        System.out.println("5.setBeanName Aware");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("6.setBeanFactory Aware");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("7.postConstruct 初始化bean");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("8.afterPropertiesSet 初始化bean");
    }

    public void init() {
        System.out.println("9.init-method 初始化bean");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("10.preDestroy 销毁bean");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("11.destroy 销毁bean");
    }

    public void shutdown() {
        System.out.println("12.destroy-method 销毁bean");
    }

    public String getProperty() {
        return property;
    }
}