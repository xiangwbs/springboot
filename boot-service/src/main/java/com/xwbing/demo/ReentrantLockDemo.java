package com.xwbing.demo;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2018/2/27 17:32
 * 作者: xiangwb
 * ---------------------------------------------------
 * 区别:
 * (1) Lock是一个接口，是JDK层面的实现；而synchronized是Java中的关键字，是Java的内置特性，是JVM层面的实现；
 * (2) synchronized 在发生异常时，会自动释放线程占有的锁，因此不会导致死锁现象发生；而Lock在发生异常时，如果没有主动通过unLock()去释放锁，则很可能造成死锁现象，因此使用Lock时需要在finally块中释放锁；
 * (3) Lock 可以让等待锁的线程响应中断，而使用synchronized时，等待的线程会一直等待下去，不能够响应中断；
 * (4) 通过Lock可以知道有没有成功获取锁，而synchronized却无法办到；
 * (5) Lock可以提高多个线程进行读操作的效率：ReentrantReadWriteLock，readLock()和writeLock()用来获取读锁和写锁；
 * 在性能上来说，如果竞争资源不激烈，两者的性能是差不多的。而当竞争资源非常激烈时（即有大量线程同时竞争），此时Lock的性能要远远优于synchronized。
 */
public class ReentrantLockDemo {
    // 注意这个地方:lock被声明为成员变量
    private final Lock lock = new ReentrantLock(true);

    public void lock() {
        lock.lock();
        try {
            //处理任务
        } catch (Exception ex) {
            //处理异常
        } finally {
            System.out.println("释放了锁...");
            lock.unlock();//释放锁
        }
    }

    public void tryLock() {
        if (lock.tryLock()) {
            try {
                System.out.println("得到了锁...");
                //处理任务
            } catch (Exception e) {
                //处理异常
            } finally {
                System.out.println("释放了锁...");
                lock.unlock();
            }
        } else {
            System.out.println("获取锁失败...");
        }
    }

    public void tryLockTime() {
        try {
            if (lock.tryLock(4, TimeUnit.SECONDS)) {//4秒
                try {
                    System.out.println("得到了锁...");
                    //处理任务
                } catch (Exception e) {
                    //处理异常
                } finally {
                    System.out.println("释放了锁...");
                    lock.unlock();
                }
            } else {
                System.out.println("放弃了对锁的获取...");
            }
        } catch (InterruptedException e) {
            System.out.println("被中断...");
        }
    }
}
