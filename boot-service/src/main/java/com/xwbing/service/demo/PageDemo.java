package com.xwbing.service.demo;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.StopWatch;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年05月23日 下午11:34
 */
@Slf4j
public class PageDemo {

    public static void selectByPageHelperAsync(int pageSize) {
        log.info("pageHelper start");
        Page<Object> page = PageHelper.startPage(1, 1);
        query();
        long count = page.getTotal();
        log.info("pageHelper count:{}", count);
        if (count == 0) {
            return;
        }
        StopWatch sw = new StopWatch();
        sw.start();
        long times = (count % pageSize == 0) ? count / pageSize : (count / pageSize + 1);
        log.info("pageHelper page:{}", times);
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, 3, 600L, TimeUnit.MICROSECONDS,
                new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("pageHelper-pool-%d").build());
        CompletableFuture[] futures = new CompletableFuture[(int) times];
        for (int i = 1; i <= times; i++) {
            PageHelper.startPage(i, pageSize);
            List<Object> list = query();
            int finalI = i;
            futures[i - 1] = CompletableFuture.runAsync(() -> {
                StopWatch pageSw = new StopWatch();
                pageSw.start();
                list.forEach(item -> {
                    // TODO: 处理业务逻辑
                });
                pageSw.stop();
                log.info("pageHelper pageNum:{} cost:{}s", finalI, pageSw.getTotalTimeSeconds());
            }, threadPool).exceptionally(throwable -> {
                log.error("pageHelper pageNum:{} error:{}", finalI, ExceptionUtils.getStackTrace(throwable));
                return null;
            });
        }
        CompletableFuture.allOf(futures).join();
        threadPool.shutdown();
        sw.stop();
        log.info("pageHelper end cost:{}m", sw.getTotalTimeSeconds() / 60);
    }

    public static void updateByPageHelper(int pageSize) {
        Page<Object> page = PageHelper.startPage(1, 1);
        query();
        long count = page.getTotal();
        log.info("updateByPageHelper count:{}", count);
        if (count == 0) {
            return;
        }
        long times = (count % pageSize == 0) ? count / pageSize : (count / pageSize + 1);
        log.info("updateByPageHelper page:{}", page);
        for (int i = 1; i <= times; i++) {
            PageHelper.startPage(1, pageSize);
            log.info("updateByPageHelper page:{} pageNum:{}", page, i);
            query().forEach(item -> {
                // TODO: 处理业务逻辑
            });
        }
    }

    public static void whilePage(int pageSize) {
        log.info("whilePage start pageSize:{}", pageSize);
        int pageNum = 1;
        StopWatch sw = new StopWatch();
        sw.start();
        while (true) {
            List<Object> list = pageQuery(pageNum, pageSize);
            log.info("whilePage pageNum:{} size:{}", pageNum, list.size());
            list.forEach(ordersResponse -> {
                // TODO: 处理业务逻辑
            });
            if (list.size() < pageSize) {
                break;
            }
            pageNum++;
        }
        sw.stop();
        log.info("whilePage end cost:{}m", sw.getTotalTimeSeconds() / 60);
    }

    public static List<Object> listPage(List<Object> list, int pageNum, int pageSize) {
        return CollUtil.page(pageNum - 1, pageSize, list);
//        int size = list.size();
//        int start = (pageNum - 1) * pageSize > size ? size : (pageNum - 1) * pageSize;
//        int end = pageSize * pageNum > size ? size : pageSize * pageNum;
//        return new ArrayList<>(list.subList(start, end));
    }

    public static void listPartition(List<Object> list, int size) {
        List<List<Object>> partition = ListUtils.partition(list, size);
        partition.forEach(objects -> {
            // TODO: 处理业务逻辑
        });
    }

    /**
     * 假接口假数据-----------------------------------------------------------------------------------------------------
     */
    private static List<Object> pageQuery(int pageNum, int pageSize) {
        return Collections.emptyList();
    }

    private static List<Object> query() {
        return Collections.emptyList();
    }

    public static void main(String[] args) {
        whilePage(10);
    }
}

