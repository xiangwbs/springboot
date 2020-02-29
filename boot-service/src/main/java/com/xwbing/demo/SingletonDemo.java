package com.xwbing.demo;


/**
 * @author xiangwb
 * @date 2020/2/29 16:12
 * 单例模式
 * 创建对象不是原子操作：
 * 1.看class对象是否加载，如果没有就先加载class对象，
 * 2.分配内存空间，初始化实例，
 * 3.调用构造函数，
 * 4.返回地址给引用
 * 而cpu为了优化程序，可能会进行指令重排序，打乱这3，4这几个步骤，导致实例内存还没分配，就被使用了。
 */
public class SingletonDemo {
    /**
     * 避免创建对象时指令重排序
     */
    private volatile static SingletonDemo singleton = null;

    private SingletonDemo() {
    }

    public static SingletonDemo getInstanse() {
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

