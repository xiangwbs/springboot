package com.xwbing.service.demo.ratelimit;

import java.io.IOException;
import java.util.Random;
import java.util.stream.IntStream;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 基于令牌桶
 */
public class GuavaDemo {
    RateLimiter rateLimiter = RateLimiter.create(10); //qps=10

    public void doRequest() {
        if (rateLimiter.tryAcquire()) { //获取一个令牌
            System.out.println(Thread.currentThread().getName() + ":正常处理");
        } else {
            System.out.println(Thread.currentThread().getName() + ":请求数量过多");
        }
    }

    public static void main(String[] args) throws IOException {
        GuavaDemo ge = new GuavaDemo();
        Random random = new Random();
        IntStream.range(1, 20).forEach(value -> new Thread(() -> {
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ge.doRequest();

        }).start());
        System.in.read();
    }
}
