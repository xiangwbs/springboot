package com.xwbing.service.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

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
        List<Object> list;
        long times = (count % pageSize == 0) ? count / pageSize : (count / pageSize + 1);
        for (int i = 1; i <= times; i++) {
            PageHelper.startPage(i, pageSize);
            list = query();
            // TODO: 处理业务逻辑
        }
    }

    public void pageHelperDealSelfSync(int pageSize) {
        Page<Object> page = PageHelper.startPage(1, 1);
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

    public void pageHelperDealSelfAsync(int pageSize) {
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

    public List<Object> page(List<Object> list, int pageNum, int pageSize) {
        if (CollectionUtils.isEmpty(list) || list.isEmpty()) {
            return Collections.emptyList();
        }
        int size = list.size();
        int start = (pageNum - 1) * pageSize > size ? size : (pageNum - 1) * pageSize;
        int end = pageSize * pageNum > size ? size : pageSize * pageNum;
        return new ArrayList<>(list.subList(start, end));
    }

    public void partition(List<Object> list, int size) {
        List<List<Object>> partition = ListUtils.partition(list, size);
        partition.forEach(objects -> {
            // TODO: 处理业务逻辑
        });
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

