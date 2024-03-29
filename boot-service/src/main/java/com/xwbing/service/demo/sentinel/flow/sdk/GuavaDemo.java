package com.xwbing.service.demo.sentinel.flow.sdk;

import java.util.Random;
import java.util.stream.IntStream;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 基于令牌桶
 */
public class GuavaDemo {
    public static void main(String[] args) {
        // qps=5
        RateLimiter rateLimiter = RateLimiter.create(5);
        Random random = new Random();
        IntStream.range(1, 20).forEach(value -> new Thread(() -> {
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 获取一个令牌
            if (rateLimiter.tryAcquire()) {
                System.out.println(Thread.currentThread().getName() + ":正常处理");
            } else {
                System.out.println(Thread.currentThread().getName() + ":请求数量过多");
            }
        }).start());
    }
}