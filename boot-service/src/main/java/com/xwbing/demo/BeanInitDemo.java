package com.xwbing.demo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author xiangwb
 *         对象初始化先后顺序
 *         父静态>子静态>父构造代码块>父构造方法>子构造代码块>子构造方法
 *         <p>
 *         InstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation>静态代码块>构造代码块>构造函数>InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation>
 *         setXXX|autowire>BeanPostProcessor.postProcessBeforeInitialization>@PostConstruct>afterPropertiesSet>init-method
 *         >BeanPostProcessor.postProcessAfterInitialization>DisposableBean.destroy>destroy-method
 */
@Component
public class BeanInitDemo implements InitializingBean, DisposableBean {
    public static void main(String[] args) {
        B ab = new B();
        ab = new B();
    }

    static {
        System.out.print("1.静态代码块");
    }

    {
        System.out.println("2.构造代码块");
    }

    public BeanInitDemo() {
        System.out.println("3.构造器");

    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("4.postConstruct 初始化bean之前执行");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("5.afterPropertiesSet 初始化bean之前执行");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("6.preDestroy bean销毁之前执行");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("7.destroy bean销毁之后执行");
    }

    public static class A {
        //静态代码块，在该类被加载入JVM时，被执行，且只会被执行一次。不能被继承。
        static {
            System.out.print("1");
        }

        //构造代码块，在类实例化时被调用，每次创建对象都会被调用，执行次序构造代码块>构造函数。
        {
            System.out.print("3");
        }

        // 构造方法，在该类被实例化的时候被执行。每个类都有隐式的空构造函数，如果定义了非空构造函数，需要自定义空构造函数
        public A() {
            System.out.print("4");
        }

    }

    public static class B extends A {
        static {
            System.out.print("2");
        }

        {
            System.out.print("5");
        }

        /**
         * 子类所有构造函数默认都会访问父类中的空构造函数。
         * 因为子类继承了父类，在使用父类内容前，要看父类如何对自己的内容进行初始化。
         * 如果父类没定义空构造函数，那子类构造函数必须用supper明确调用父类的哪个构造函数。
         */
        public B() {
            //            super();
            System.out.print("6");
        }
    }
}

