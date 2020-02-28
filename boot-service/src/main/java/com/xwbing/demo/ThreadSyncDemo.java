package com.xwbing.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 说明:
 * 多线程并发安全问题
 * 当多个线程访问同个资源时，由于线程切换的不确定性，
 * 可能导致代码逻辑上的混乱，严重时可能导致系统崩溃
 * 项目名称: zdemo
 * 创建日期: 2017年2月20日 下午2:34:05
 * 作者: xiangwb
 */

public class ThreadSyncDemo {
    public static void main(String[] args) {
        /*
         * 同步代码块
         */
        final Shop s = new Shop();//保证对象一致
        Thread t1 = new Thread(s::buy);
        Thread t2 = new Thread(s::buy);
        t1.start();
        t2.start();


        /*
         * 静态方法锁
         */
        Thread tt1 = new Thread(ThreadSyncDemo::doSome);
        Thread tt2 = new Thread(ThreadSyncDemo::doSome);
        tt1.start();
        tt2.start();

        /*
         * 互斥
         */
        final Foo f = new Foo();//保证对象一致
        Thread ttt1 = new Thread(f::methodA);
        Thread ttt2 = new Thread(f::methodB);
        ttt1.start();
        ttt2.start();

        /**
         * arraylist,linkedlist,hashset都不是线程安全的
         * 线程安全的list集合有一个vector
         * hashmap不是线程安全的，线程安全的是hashtable
         * collections提供类相应的静态方法，可以将现有的集合或map转换为线程安全的
         */
        List<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list = Collections.synchronizedList(list);//将现有的list集合转换为线程安全的集合
        Set<String> set = new HashSet<>(list);
        set = Collections.synchronizedSet(set);   //转换为线程安全的set集合
        Map<String, Integer> map = new HashMap<>();
        map.put("语文", 99);
        map.put("数学", 95);
        map.put("英语文", 58);
        map = Collections.synchronizedMap(map);//转换为线程安全的map

    }

    /*
     * 静态方法锁：
     * synchronized修饰静态方法肯定同步，因为静态方法只有一份
     */
    private synchronized static void doSome() {
        Thread t = Thread.currentThread();
        System.out.println(t + "正在执行doSome");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(t + "执行doSome完毕");

    }
}

class Shop {
    void buy() {
        Thread t = Thread.currentThread();
        try {
            System.out.println(t + "买");
            Thread.sleep(5000);
            /*
             * 同步代码块：
             * 同步块需要指定同步监视器（上锁对象）
             * 若希望该同步块有效，必须保证多个线程看到的锁对象相同
             * 通常使用this即可
             */
            synchronized (this) {
                System.out.println(t + "试");
                Thread.sleep(5000);
            }
            System.out.println(t + "over");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Foo {
    /*
     * 互斥锁
     * synchronized修饰的是两段代码，但是锁对象相同
     * 那么这两端代码就是互斥的
     */
    synchronized void methodA() {
        Thread t = Thread.currentThread();
        System.out.println(t + "开始执行a");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(t + "a结束");
    }

    synchronized void methodB() {
        Thread t = Thread.currentThread();
        System.out.println(t + "开始执行B");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(t + "B结束");
    }
}
