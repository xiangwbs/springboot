package com.xwbing.service.demo;

import org.springframework.util.StopWatch;

/**
 * @author xiangwb
 * @date 2020/3/5 23:06
 */
public class StopWatchDemo {
    public static void main(String[] args) throws InterruptedException {
        StopWatch sw = new StopWatch("test");
        sw.start("task1");
        // do something
        Thread.sleep(100);
        sw.stop();
        sw.start("task2");
        // do something
        Thread.sleep(200);
        sw.stop();
        System.out.println(sw.prettyPrint());
        System.out.println(sw.shortSummary());
        System.out.println(sw.getTotalTimeMillis());
    }
}
