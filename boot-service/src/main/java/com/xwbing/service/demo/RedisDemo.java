package com.xwbing.service.demo;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import lombok.RequiredArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2022年08月02日 9:01 PM
 */
@RequiredArgsConstructor
@Service
public class RedisDemo {
    private final RedisTemplate redisTemplate;
    private final RedissonClient redissonClient;

    //@formatter:off
    private static String script = "" +
            "local lockSet = redis.call('exists', KEYS[1])" +
            "if lockSet == 0 then" +
            "redis.call('set', KEYS[1], ARGV[1])" +
            // 设置过期时间，防止死锁
            "redis.call('expire', KEYS[1], ARGV[2])" +
            "end" +
            "return lockSet";
    //@formatter:on

    public String payLua(Long orderId) {
        try {
            Long lock = (Long)redisTemplate
                    .execute(RedisScript.of(script, Long.class), Collections.singletonList("pay:" + orderId), "1", 30);
            if (lock == 0) {
                //模拟支付业务代码执行10s
                Thread.sleep(10000);
                //处理完业务逻辑删除锁  异常了
                redisTemplate.delete("pay:" + orderId);
            }
            return lock == 0 ? "正常支付完毕" : "请稍等，已经有人在支付！！";
        } catch (Exception exception) {
            redisTemplate.delete("lock" + orderId);
            return "系统异常";
        }
    }

    public String payRedisson(Long orderId) throws InterruptedException {
        RLock rlock = redissonClient.getLock("order_lock" + orderId);
        if (rlock.tryLock(10, -1, TimeUnit.SECONDS)) {
            System.out.println("获取锁成功");
            Thread.sleep(10000);
            rlock.unlock();
            return "处理完成";
        } else {
            System.out.println("获取锁失败");
            return "请稍等，已经有人在支付！！";
        }
    }

    public String payMultiLock(Long orderId) throws InterruptedException {
        // 可以使不同的客户端
        RLock rLock = redissonClient.getLock("order_lock" + orderId);
        RLock rLock1 = redissonClient.getLock("order_lock" + orderId);
        RedissonRedLock lock = new RedissonRedLock(rLock, rLock1);
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            System.out.println("获取锁成功");
            Thread.sleep(10000);
            lock.unlock();
            return "处理完成";
        } else {
            System.out.println("获取锁失败");
            return "请稍等，已经有人在支付！！";
        }
    }

    public void pipelining() {
        redisTemplate.executePipelined((RedisCallback<Object>)connection -> {
            // 批量执行的操作
            for (int i = 0; i < 1000; i++) {
                connection.stringCommands().set(("key" + i).getBytes(), String.valueOf(i).getBytes());
            }
            // 注意，RedisCallback返回的值必须为null，因为为了返回pipeline命令的结果，该值被丢弃
            return null;
        });
    }

    public void hashedWheelTimer() {
        // 创建了一个hash轮定时器
        Timer timer = new HashedWheelTimer();
        // 提交一个任务，让它在10s后执行
        timer.newTimeout(timeout -> System.out.println("10s后执行任务1"), 10, TimeUnit.SECONDS);
        // 再提交一个任务，让它在20s后执行
        timer.newTimeout(timeout -> System.out.println("20s后执行任务2"), 20, TimeUnit.SECONDS);
        System.out.println("主线程结束");
    }
}