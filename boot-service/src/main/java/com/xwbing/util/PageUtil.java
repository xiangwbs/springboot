package com.xwbing.util;

import java.util.*;

/**
 * 列表分页工具类
 *
 * @author xiangwb
 */
public class PageUtil {
    public static Map<String, Object> page(List list, int currentPage, int pageSize) {
        Map<String, Object> pageMap = new HashMap<>(5);
        int size = list.size();
        pageMap.put("count", size);
        int totalPage;
        if (size == 0) {
            pageMap.put("data", Collections.emptyList());
            totalPage = 0;
        } else {
            int start = (currentPage - 1) * pageSize;
            start = start > size ? size : start;
//            if (start >= size) {
//                pageMap.put("data", Collections.emptyList());
//            }
            int end = pageSize * currentPage > size ? size : pageSize * currentPage;
            pageMap.put("data", list.subList(start, end));
            totalPage = size % pageSize == 0 ? size / pageSize : size / pageSize + 1;
        }
        pageMap.put("totalPage", totalPage);
        return pageMap;
    }
}
