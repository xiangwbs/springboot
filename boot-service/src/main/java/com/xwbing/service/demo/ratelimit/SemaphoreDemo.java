package com.xwbing.service.demo.ratelimit;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 基于计数器
 */
public class SemaphoreDemo {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Semaphore semaphore = new Semaphore(5); //5个令牌
        for (int i = 0; i < 20; i++) {
            final int NO = i;
            Runnable runnable = () -> { //表示线程的执行逻辑
                try {
                    semaphore.acquire(); //在执行之前，先获取一个令牌
                    System.out.println(Thread.currentThread().getName() + ":执行业务逻辑:" + NO);
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println(Thread.currentThread().getName() + ":释放令牌");
                    semaphore.release(); //释放令牌
                }
            };
            executorService.execute(runnable);
        }
        executorService.shutdown();
    }
}
