package com.xwbing.service.demo;

import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiangwb
 */
@Slf4j
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
        System.out.println("StopWatchDemo cost:{}s"+sw.getTotalTimeSeconds());
        System.out.println("StopWatchDemo end {}"+ sw.prettyPrint());
    }
}
