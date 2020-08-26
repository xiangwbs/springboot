package com.xwbing.service.demo;

import java.io.Serializable;

import com.xwbing.service.exception.BusinessException;

/**
 * 单例模式
 * 创建对象不是原子操作：
 * 1.看class对象是否加载，如果没有就先加载class对象，
 * 2.分配内存空间，初始化实例，
 * 3.调用构造函数，
 * 4.返回地址给引用
 * 而cpu为了优化程序，可能会进行指令重排序，打乱这3，4这几个步骤，导致实例内存还没分配，就被使用了。
 *
 * @author xiangwb
 */
public class SingletonDemo implements Serializable {
    /**
     * 避免创建对象时指令重排序
     */
    private volatile static SingletonDemo singleton;

    private SingletonDemo() {
        //避免通过反射调用构造方法
        if (singleton != null) {
            throw new BusinessException("SingletonDemo constructor is called...");
        }
    }

    public static SingletonDemo getInstance() {
        //第一次判空，保证不必要的同步
        if (singleton == null) {
            //保证每次只有一个线程创建实例
            synchronized (SingletonDemo.class) {
                //第二次判空为了在null的情况下创建实例
                if (singleton == null) {
                    singleton = new SingletonDemo();
                }
            }
        }
        return singleton;
    }

    /**
     * 添加反序列化默认获取对象实例方法
     *
     * @return
     */
    private Object readResolve() {
        return getInstance();
    }

    /**
     * 饿汉式
     */
    public static class Single {
        private static Single single = new Single();

        private Single() {
        }

        public static Single getInstanse() {
            return single;
        }
    }
}

