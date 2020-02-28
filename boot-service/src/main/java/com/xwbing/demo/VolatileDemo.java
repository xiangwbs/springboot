package com.xwbing.demo;

import java.util.concurrent.CompletableFuture;

/**
 * @author xiangwb
 * @date 2018/12/3 21:31
 * 多线程特性一：可见性
 * synchronize和锁也可以保证可见性
 * 保证读取主内存副本到工作内存和刷新到主内存的步骤是原子的
 * volatile修饰的变量值直接存在main memory里面，子线程对该变量的读写直接写入main memory，而不是像其它变量一样在local thread里面产生一份copy。(可以理解为直接操作主内存)
 * volatile能保证所修饰的共享变量对于多个线程可见性，即只要被修改，其它线程读到的一定是最新的值。
 * *
 * （有序性：禁止指令重排序，保证被volatile修饰变量这一行之前的代码肯定是执行了的）
 */
public class VolatileDemo {
    volatile
    int a = 0;

    void set(int a) {
        this.a = a;
    }

    int get() {
        return this.a;
    }

    public static void main(String[] args) throws InterruptedException {
        /**
         * 如果变量a没有volatile修饰，set方法涉及给a赋值和更新主内存a的值，
         * 即使线程1先运行，可能出现线程1只给a赋值，还没有更新到主内存，切换到线程2，那么线程2打印的是旧值0。
         */
        VolatileDemo volatileDemo = new VolatileDemo();
        CompletableFuture.runAsync(() -> volatileDemo.set(1));//线程1
        CompletableFuture.runAsync(() -> System.out.println(volatileDemo.get()));//线程2

        /**
         * 如果isRunning变量没有volatile修饰，由于子线程工作内存不知道主内存值修改，会一直死循环下去
         */
        Flog flog = new Flog();
        flog.start();
        Thread.sleep(1);
        flog.setRunning(false);
        System.out.println("main end isRunning:" + flog.isRunning());

    }
}

/**
 * 场景1：状态标记
 */
class Flog extends Thread {
    volatile
    boolean isRunning = true;

    boolean isRunning() {
        return isRunning;
    }

    void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public void run() {
        System.out.println("进入到run方法中了");
        /*
         * jvm会提升优化为
         * if (isRunning)
         *    while (true) {}
         */
        while (isRunning) {
//            System.out.println("");//由于动用了System，每次都会重新优化
        }
        System.out.println("线程执行完成了");
    }
}

/**
 * 场景二：结合synchronized实现开销较低的读-写锁
 */
class RW {
    private volatile int value;

    int getValue() {
        return this.value;
    }

    synchronized int increase() {
        return value++;
    }
}