package com.xwbing.service.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.util.StopWatch;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年05月23日 下午11:34
 */
@Slf4j
public class PageDemo {

    public static void pageHelper(int pageSize) {
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
        CompletableFuture[] futures = new CompletableFuture[(int)times];
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
                log.error("pageHelper pageNum:{} error", finalI, throwable);
                return null;
            });
        }
        CompletableFuture.allOf(futures).join();
        threadPool.shutdown();
        sw.stop();
        log.info("pageHelper end cost:{}m", sw.getTotalTimeSeconds() / 60);
    }

    public static void pageHelperDealSelfSync(int pageSize) {
        Page<Object> page = PageHelper.startPage(1, 1);
        query();
        long count = page.getTotal();
        if (count == 0) {
            return;
        }
        List<Object> list;
        long times = (count % pageSize == 0) ? count / pageSize : (count / pageSize + 1);
        for (int i = 1; i <= times; i++) {
            PageHelper.startPage(1, pageSize);
            list = query();
            // TODO: 处理业务逻辑
        }
    }

    public static void pageHelperDealSelfAsync(int pageSize) {
        Page<Object> checkPage = PageHelper.startPage(1, 1);
        query();
        long count = checkPage.getTotal();
        if (count == 0) {
            return;
        }
        List<Object> preList = new ArrayList<>();
        List<Object> postList = new ArrayList<>();
        long times = (count % pageSize == 0) ? count / pageSize : (count / pageSize + 1);
        for (int i = 1; i <= times; i++) {
            PageHelper.startPage(i, pageSize);
            List<Object> list = query();
            preList.addAll(list);
        }
        preList.forEach(object -> {
            postList.add(object);
            if (postList.size() >= pageSize) {
                List<Object> lists = new ArrayList<>(postList);
                postList.clear();
                CompletableFuture.runAsync(() -> lists.forEach(o -> {
                    // TODO: 处理业务逻辑
                }));
            }
        });
        if (!postList.isEmpty()) {
            CompletableFuture.runAsync(() -> postList.forEach(o -> {
                // TODO: 处理业务逻辑
            }));
        }
    }

    public static void whilePage(int pageSize) {
        log.info("whilePage start");
        List<CompletableFuture> futures = new ArrayList<>();
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, 3, 600L, TimeUnit.MICROSECONDS,
                new LinkedBlockingQueue<>(), new ThreadFactoryBuilder().setNameFormat("whilePage-pool-%d").build());
        int pageNum = 1;
        StopWatch sw = new StopWatch();
        sw.start();
        while (true) {
            List<Object> list = pageQuery(pageNum, pageSize);
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            int finalPageNum = pageNum;
            futures.add(CompletableFuture.runAsync(() -> {
                StopWatch pageSw = new StopWatch();
                pageSw.start();
                list.forEach(ordersResponse -> {
                    // TODO: 处理业务逻辑
                });
                pageSw.stop();
                log.info("whilePage pageNum:{} cost:{}s", finalPageNum, pageSw.getTotalTimeSeconds());
            }, threadPool).exceptionally(throwable -> {
                log.error("whilePage pageNum:{} error", finalPageNum, throwable);
                return null;
            }));
            pageNum++;
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
        threadPool.shutdown();
        sw.stop();
        log.info("whilePage end cost:{}m", sw.getTotalTimeSeconds() / 60);
    }

    public static List<Object> page(List<Object> list, int pageNum, int pageSize) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        int size = list.size();
        int start = (pageNum - 1) * pageSize > size ? size : (pageNum - 1) * pageSize;
        int end = pageSize * pageNum > size ? size : pageSize * pageNum;
        return new ArrayList<>(list.subList(start, end));
    }

    public static void partition(List<Object> list, int size) {
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

