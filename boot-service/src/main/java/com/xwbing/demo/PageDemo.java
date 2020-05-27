package com.xwbing.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.collections.CollectionUtils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年05月23日 下午11:34
 */
public class PageDemo {

    public void pageHelper(int pageSize) {
        Page<Object> page = PageHelper.startPage(1, 1);
        long count = page.getTotal();
        if (count == 0) {
            return;
        }
        long times = (count % pageSize == 0) ? count / pageSize : (count / pageSize + 1);
        for (int i = 1; i <= times; i++) {
            PageHelper.startPage(i, pageSize);
            List<Object> list = query();
            // TODO: 处理业务逻辑
        }
    }

    public void pageQuery(int pageSize) {
        int pageNum = 0;
        List<Object> responseList;
        do {
            pageNum++;
            responseList = pageQuery(pageNum, pageSize);
            if (CollectionUtils.isNotEmpty(responseList)) {
                responseList.forEach(ordersResponse -> {
                    // TODO: 处理业务逻辑
                });
            }
        } while (CollectionUtils.isNotEmpty(responseList));
    }

    public void page(int pageSize) {
        Page<Object> checkPage = PageHelper.startPage(1, 1);
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

    public List<Object> page(List<Object> list, int pageNum, int pageSize) {
        if (CollectionUtils.isEmpty(list) || list.isEmpty()) {
            return Collections.emptyList();
        }
        int size = list.size();
        int start = (pageNum - 1) * pageSize > size ? size : (pageNum - 1) * pageSize;
        int end = pageSize * pageNum > size ? size : pageSize * pageNum;
        return new ArrayList<>(list.subList(start, end));
    }

    /**
     * 假接口假数据-----------------------------------------------------------------------------------------------------
     */
    private List<Object> pageQuery(int pageNum, int pageSize) {
        return Collections.emptyList();
    }

    private List<Object> query() {
        return Collections.emptyList();
    }
}

