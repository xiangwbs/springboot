package com.xwbing.service.demo.sentinel.ratelimit.sdk;

import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

/**
 * 基于计数器
 */
public class SemaphoreDemo {
    public static void main(String[] args) {
        // 5个令牌
        Semaphore semaphore = new Semaphore(5);
        IntStream.range(1, 20).forEach(value -> new Thread(() -> {
            try {
                // 在执行之前，先获取一个令牌
                semaphore.acquire();
                System.out.println(Thread.currentThread().getName() + ":执行业务逻辑");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release(); //释放令牌
                System.out.println(Thread.currentThread().getName() + ":释放令牌");
            }
        }).start());
    }
}