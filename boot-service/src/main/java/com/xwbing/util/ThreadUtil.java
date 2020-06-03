package com.xwbing.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * DopThreadUtil.build().xxxPool()
 * 根据任务类型是IO密集型还是CPU密集型、CPU核数，来设置合理的线程池大小、队列大小、拒绝策略。
 * 建议：刚开始可以保守设置，最适合自己场景的参数需要压测和调优。
 *
 * @author daofeng
 * @version $
 * @since 2020年03月31日 07:24
 */
public class ThreadUtil {
    private static volatile ThreadUtil threadUtil;
    private ThreadPoolExecutor singleThreadPool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MICROSECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("singlePool").build());
    private ThreadPoolExecutor excelThreadPool = new ThreadPoolExecutor(5, 5, 600L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("excel").build());

    private ThreadUtil() {

    }

    public static ThreadUtil build() {
        if (threadUtil == null) {
            synchronized (ThreadUtil.class) {
                if (threadUtil == null) {
                    threadUtil = new ThreadUtil();
                }
            }
        }
        return threadUtil;
    }

    public ThreadPoolExecutor singleThreadPool() {
        return singleThreadPool;
    }

    public ThreadPoolExecutor excelThreadPool() {
        return excelThreadPool;
    }
}
