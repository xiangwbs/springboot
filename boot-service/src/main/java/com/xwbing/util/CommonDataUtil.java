package com.xwbing.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 公共数据类
 *
 * @author xiangwb
 */
public class CommonDataUtil {
    private static Map<String, JSONObject> token = new ConcurrentHashMap<>();
    private static final long MINUTES = 1000 * 60;
    public static final int MINUTE = 1;
    public static final int HOUR = MINUTE * 60;
    public static final int DAY = HOUR * 24;

    private CommonDataUtil() {
    }

    /**
     * 存数据
     *
     * @param key
     * @param value
     */
    public static void setData(String key, Object value) {
        JSONObject object = new JSONObject();
        object.put("value", value);
        object.put("expiry", -1);
        token.put(key, object);
    }

    /**
     * 存数据,带有效期
     *
     * @param key    key
     * @param value  value
     * @param minute 分钟
     */
    public static void setData(String key, Object value, int minute) {
        long currentTimeMillis = System.currentTimeMillis();
        JSONObject object = new JSONObject();
        object.put("value", value);
        object.put("expiry", currentTimeMillis + minute * MINUTES);
        token.put(key, object);
    }

    /**
     * 获取数据
     *
     * @param key
     * @return
     */
    public static Object getData(String key) {
        long currentTimeMillis = System.currentTimeMillis();
        JSONObject object = token.get(key);
        if (object != null) {
            Object value = object.get("value");
            long expiry = object.getLongValue("expiry");
            if (-1 == expiry) {
                return value;
            } else {
                if (expiry >= currentTimeMillis) {
                    return value;
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    /**
     * 删除某条数据
     *
     * @param key
     */
    public static void clearData(String key) {
        token.remove(key);
    }

    /**
     * 删除所有过期数据
     */
    public static void clearExpiryData() {
        long currentTimeMillis = System.currentTimeMillis();
        Set<Map.Entry<String, JSONObject>> entries = token.entrySet();
        Iterator<Map.Entry<String, JSONObject>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, JSONObject> next = iterator.next();
            JSONObject object = next.getValue();
            if (object != null) {
                long expiry = object.getLongValue("expiry");
                if (-1 != expiry) {
                    if (expiry < currentTimeMillis) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    /**
     * 清空所有数据
     */
    public static void clearAllData() {
        token.clear();
    }

    public static void main(String[] args) {
        CommonDataUtil.setData("a", "a", 1);
        CommonDataUtil.setData("b", "b");
        Object a = CommonDataUtil.getData("a");
        Object b = CommonDataUtil.getData("b");
        CommonDataUtil.clearExpiryData();
    }
}
