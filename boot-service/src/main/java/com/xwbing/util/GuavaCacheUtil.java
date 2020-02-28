package com.xwbing.util;

import com.google.common.cache.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangwb
 * @date 2018/10/8 21:37
 * @description
 */
@Slf4j
public class GuavaCacheUtil {
    /**
     * 缓存项最大数量
     */
    private static final long SIZE = 10000;
    /**
     * 缓存时间：小时
     */
    private static final long TIME = 24;

    /**
     * 缓存操作对象
     */
    private static Cache<String, Object> GLOBAL_CACHE;

    static {
        GLOBAL_CACHE = CacheBuilder.newBuilder().maximumSize(SIZE).expireAfterAccess(TIME, TimeUnit.HOURS)
                .removalListener((RemovalListener<String, Object>) rn -> {
                    if (log.isDebugEnabled())
                        log.debug("Guava Cache缓存回收成功，键：{}, 值：{}", rn.getKey(), rn.getValue());
                }).recordStats().build();
    }


    /**
     * 设置缓存值
     *
     * @param key
     * @param value
     */
    public static void put(String key, Object value) {
        GLOBAL_CACHE.put(key, value);
        if (log.isDebugEnabled()) {
            log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
        }
    }

    /**
     * 批量设置缓存值
     *
     * @param map
     */
    public static void putAll(Map<String, Object> map) {
        GLOBAL_CACHE.putAll(map);
        if (log.isDebugEnabled()) {
            log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
        }
    }

    /**
     * 获取缓存值
     *
     * @param key
     * @return
     */
    public static Object get(String key, Callable<Object> callable) {
        Object obj = null;
        try {
            obj = GLOBAL_CACHE.get(key, callable);
            if (log.isDebugEnabled())
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
        } catch (Exception e) {
            log.error("获取缓存值出错", e);
        }
        return obj;
    }

    /**
     * 获取缓存值
     *
     * @param key
     * @return
     */
    public static Object getIfPresent(String key) {
        Object obj = null;
        try {
            obj = GLOBAL_CACHE.getIfPresent(key);
            if (log.isDebugEnabled())
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
        } catch (Exception e) {
            log.error("获取缓存值出错", e);
        }
        return obj;
    }

    /**
     * 移除缓存
     *
     * @param key
     */
    public static void remove(String key) {
        try {
            GLOBAL_CACHE.invalidate(key);
            if (log.isDebugEnabled())
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
        } catch (Exception e) {
            log.error("移除缓存出错", e);
        }
    }

    /**
     * 批量移除缓存
     *
     * @param keys
     */
    public static void removeAll(List<String> keys) {
        try {
            GLOBAL_CACHE.invalidateAll(keys);
            if (log.isDebugEnabled())
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
        } catch (Exception e) {
            log.error("批量移除缓存出错", e);
        }
    }

    /**
     * 清空所有缓存
     */
    public static void removeAll() {
        try {
            GLOBAL_CACHE.invalidateAll();
            if (log.isDebugEnabled())
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
        } catch (Exception e) {
            log.error("清空所有缓存出错", e);
        }
    }

    /**
     * 获取缓存项数量
     *
     * @return
     */
    public static long size() {
        long size = 0;
        try {
            size = GLOBAL_CACHE.size();
            if (log.isDebugEnabled())
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
        } catch (Exception e) {
            log.error("获取缓存项数量出错", e);
        }
        return size;
    }

    /**
     * 获取所有缓存项的键
     *
     * @return
     */
    public static List<String> keys() {
        List<String> list = new ArrayList<>();
        try {
            ConcurrentMap<String, Object> map = GLOBAL_CACHE.asMap();
            for (Map.Entry<String, Object> item : map.entrySet())
                list.add(item.getKey());
            if (log.isDebugEnabled())
                log.debug("缓存命中率：{}，新值平均加载时间：{}", getHitRate(), getAverageLoadPenalty());
        } catch (Exception e) {
            log.error("获取所有缓存项的键出错", e);
        }
        return list;
    }

    /**
     * 缓存命中率
     *
     * @return
     */
    public static double getHitRate() {
        return GLOBAL_CACHE.stats().hitRate();
    }

    /**
     * 加载新值的平均时间，单位为纳秒
     *
     * @return
     */
    public static double getAverageLoadPenalty() {
        return GLOBAL_CACHE.stats().averageLoadPenalty();
    }

    /**
     * 缓存项被回收的总数，不包括显式清除
     *
     * @return
     */
    public static long getEvictionCount() {
        return GLOBAL_CACHE.stats().evictionCount();
    }

    public static void main(String[] args) {
        GuavaCacheUtil.put("aa", "aa");
        Object aa = GuavaCacheUtil.getIfPresent("aa");
        GuavaCacheUtil.remove("aa");
        Object bb = GuavaCacheUtil.get("bb", () -> {
            return "bb";
        });

    }
}
