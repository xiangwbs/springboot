package com.xwbing.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2018/7/3 下午8:57
 * 作者: xiangwb
 * 说明: 统计分析
 */
@Slf4j
public class CountDemo {
    public static final String ITEM = "aa,bb";

    /**
     * 饼图
     *
     * @return
     */
    public static JSONArray pie(List<JSONObject> list) {
        JSONArray result = new JSONArray();
        if (list == null) {
            list = Collections.EMPTY_LIST;
        }
        Map<String, List<JSONObject>> collect = list.stream().collect(Collectors.groupingBy(obj -> obj.getString("item")));
        JSONObject obj;
        for (String item : ITEM.split(",")) {
            obj = new JSONObject();
            obj.put("name", item);
            List<JSONObject> sample = collect.get(item);
            if (sample != null) {
                int sum = sample.stream().mapToInt(value -> value.getInteger("value")).sum();
                obj.put("value", sum);
            } else {
                obj.put("value", 0);
            }
            result.add(obj);
        }
        return result;
    }

    /**
     * eCharts柱状图/折线图
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Map<String, Object> eChartsBarOrLine(String startDate, String endDate, List<JSONObject> list) {
        Map<String, Object> result = new HashMap<>();
        //统计时间
        List<String> days = listDate(startDate, endDate);
        result.put("xAxis", days);
        if (list == null) {
            list = Collections.EMPTY_LIST;
        }
        Map<String, List<JSONObject>> collect = list.stream().collect(Collectors.groupingBy(obj -> obj.getString("day")));
        JSONObject obj;
        JSONArray array;
        JSONArray series = new JSONArray();
        //统计数据
        for (String item : ITEM.split(",")) {
            obj = new JSONObject();
            obj.put("name", item);
            array = new JSONArray();
            for (String day : days) {
                List<JSONObject> sample = collect.get(day);
                if (sample != null) {
                    int sum = sample.stream().filter(it -> item.equals(it.getString("item"))).mapToInt(it -> it.getInteger("value")).sum();
                    array.add(sum);
                } else {
                    array.add(0);
                }
            }
            obj.put("data", array);
            series.add(obj);
        }
        result.put("series", series);
        return result;
    }

    /**
     * g2柱状图
     *
     * @param startDate
     * @param endDate
     * @param list
     * @return
     */
    public static Map<String, Object> g2Bar(String startDate, String endDate, List<JSONObject> list) {
        Map<String,Object> result = new HashMap<>();
        List<String> days = listDate(startDate, endDate);
        result.put("fields", days);
        if (list == null) {
            list = Collections.EMPTY_LIST;
        }
        JSONArray array = new JSONArray();
        Map<String, List<JSONObject>> collect = list.stream().collect(Collectors.groupingBy(obj -> obj.getString("day")));
        JSONObject obj;
        for (String item : ITEM.split(",")) {
            obj = new JSONObject();
            obj.put("name", item);
            for (String day : days) {
                List<JSONObject> sample = collect.get(day);
                if (sample != null) {
                    int sum = sample.stream().filter(it -> item.equals(it.getString("item"))).mapToInt(it -> it.getInteger("value")).sum();
                    obj.put(day, sum);

                } else {
                    obj.put(day, 0);
                }
            }
            array.add(obj);
        }
        result.put("data", array);
        return result;
    }

    /**
     * g2折线图
     *
     * @param startDate
     * @param endDate
     * @param list
     * @return
     */
    public static JSONArray g2Line(String startDate, String endDate, List<JSONObject> list) {
        JSONArray result = new JSONArray();
        List<String> days = listDate(startDate, endDate);
        if (list == null) {
            list = Collections.EMPTY_LIST;
        }
        Map<String, List<JSONObject>> collect = list.stream().collect(Collectors.groupingBy(obj -> obj.getString("day")));
        JSONObject obj;
        for (String day : days) {
            obj = new JSONObject();
            obj.put("day", day);
            List<JSONObject> sample = collect.get(day);
            for (String item : ITEM.split(",")) {
                if (sample != null) {
                    int sum = sample.stream().filter(it -> item.equals(it.getString("item"))).mapToInt(it -> it.getInteger("value")).sum();
                    obj.put(item, sum);
                } else {
                    obj.put(item, 0);
                }
            }
            result.add(obj);
        }
        return result;
    }

    /**
     * 模拟数据列表
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private static List<JSONObject> listByDate(String startDate, String endDate) {
        List<JSONObject> result = new ArrayList<>();
        List<String> list = listDate(startDate, endDate);
        JSONObject obj;
        int i = 0;
        for (String object : list) {
            obj = new JSONObject();
            obj.put("day", object);
            obj.put("value", ++i);
            String[] array = ITEM.split(",");
            obj.put("item", array[new Random().nextInt(array.length)]);
            result.add(obj);
        }
        return result;
    }

    /**
     * 获取日期列表
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private static List<String> listDate(String startDate, String endDate) {
        List<String> days = new ArrayList<>();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        long len = ChronoUnit.DAYS.between(start, end);
        for (long i = 0; i <= len; i++) {
            days.add(start.plusDays(i).toString());
        }
        return days;
    }

    public static void main(String[] args) {
        String startDate = "2018-01-01";
        String endDate = "2018-01-03";
        List<JSONObject> list = listByDate(startDate, endDate);
//        List<JSONObject> list = new ArrayList<>();
//        List<JSONObject> list = null;
        log.info("pie:{}", JSON.toJSONString(pie(list), SerializerFeature.PrettyFormat));
        log.info("eChartsBarOrLine{}", JSON.toJSONString(eChartsBarOrLine(startDate, endDate, list), SerializerFeature.PrettyFormat));
        log.info("g2Bar{}" + JSON.toJSONString(g2Bar(startDate, endDate, list), SerializerFeature.PrettyFormat));
        log.info("g2Line{}", JSON.toJSONString(g2Line(startDate, endDate, list), SerializerFeature.PrettyFormat));
    }
}
