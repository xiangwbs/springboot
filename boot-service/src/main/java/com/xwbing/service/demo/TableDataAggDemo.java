package com.xwbing.service.demo;

import cn.hutool.core.collection.ListUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 表格数据聚合
 *
 * @author daofeng
 * @version $
 * @since 2024年07月18日 11:09 AM
 */
public class TableDataAggDemo {
    /**
     * @param dimensionList 维度列表
     * @param metricList    指标列表
     * @param dataList
     * @return
     */
    public static List<Map<String, Object>> aggData(List<String> dimensionList, List<String> metricList, List<Map<String, Object>> dataList) {
        // 获取维度聚合列表
        List<String> groupDimensionList = dataList.stream()
                .map(data -> groupDimensionData(dimensionList, data))
                .distinct()
                .collect(Collectors.toList());
        // 分组数据
        Map<String, List<Map<String, Object>>> dataListMap = dataList.stream()
                .collect(Collectors.groupingBy(data -> groupDimensionData(dimensionList, data)));
        // 聚合数据
        return groupDimensionList.stream()
                .map(groupDimension -> {
                    Map<String, Object> dataMap = new LinkedHashMap<>();
                    String[] dimensionArray = groupDimension.split("-");
                    for (int i = 0; i < dimensionArray.length; i++) {
                        dataMap.put(dimensionList.get(i), dimensionArray[i]);
                    }
                    if (CollectionUtils.isNotEmpty(metricList)) {
                        List<Map<String, Object>> metricDataList = dataListMap.getOrDefault(groupDimension, Collections.emptyList());
                        metricList.forEach(metric -> {
                                    double sum = metricDataList.stream().mapToDouble(value -> Double.parseDouble(String.valueOf(value.getOrDefault(metric, 0)))).sum();
                                    dataMap.put(metric, sum);
                                }
                        );
                    }
                    return dataMap;
                }).collect(Collectors.toList());
    }


    /**
     * @param dimensionCount 维度数量
     * @param dataList
     * @return
     */
    public static List<List<Object>> aggData(Integer dimensionCount, List<List<Object>> dataList) {
        List<String> dimensionList = dataList.stream()
                .map(data -> groupDimensionData(dimensionCount, data))
                .distinct()
                .collect(Collectors.toList());
        Map<Object, List<List<Object>>> metricMap = dataList.stream()
                .collect(Collectors.groupingBy((data -> groupDimensionData(dimensionCount, data))));
        List<List<Object>> newList = new ArrayList<>();
        int size = dataList.get(0).size();
        dimensionList.forEach(dimension -> {
            List<Object> s = new ArrayList<>(Arrays.asList(dimension.split("-")));
            List<List<Object>> lists = metricMap.get(dimension);
            for (int i = dimensionCount; i < size; i++) {
                int finalI = i;
                double sum = lists.stream().mapToDouble(obj -> Double.parseDouble(String.valueOf(obj.get(finalI)))).sum();
                s.add(sum);
            }
            newList.add(s);
        });
        return newList;
    }

    /**
     * 聚合维度数据
     *
     * @param dimensionList
     * @param data
     * @return
     */
    private static String groupDimensionData(List<String> dimensionList, Map<String, Object> data) {
        return dimensionList.stream()
                .map(dimension -> data.getOrDefault(dimension, "").toString())
                .collect(Collectors.joining("-"));
    }

    /**
     * 聚合维度数据
     *
     * @param dimensionCount
     * @param data
     * @return
     */
    private static String groupDimensionData(Integer dimensionCount, List<Object> data) {
        return IntStream.range(0, dimensionCount)
                .mapToObj(data::get)
                .map(String::valueOf)
                .collect(Collectors.joining("-"));
    }

    public static List<List<Object>> convertData(List<Map<String, Object>> dataList) {
        return dataList.stream()
                .map(dataMap -> new ArrayList<>(dataMap.values()))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, Object> dataMap1 = new LinkedHashMap<>();
        dataMap1.put("name", "a");
        dataMap1.put("alias", "a");
        dataMap1.put("age", 12);
        dataList.add(dataMap1);
        Map<String, Object> dataMap2 = new LinkedHashMap<>();
        dataMap2.put("name", "a");
        dataMap2.put("alias", "a");
        dataMap2.put("age", 18);
        dataList.add(dataMap2);
        Map<String, Object> dataMap3 = new LinkedHashMap<>();
        dataMap3.put("name", "a");
        dataMap3.put("alias", "b");
        dataMap3.put("age", 12);
        dataList.add(dataMap3);
        Map<String, Object> dataMap4 = new LinkedHashMap<>();
        dataMap4.put("name", "b");
        dataMap4.put("alias", "a");
        dataMap4.put("age", 12);
        dataList.add(dataMap4);
        Map<String, Object> dataMap5 = new LinkedHashMap<>();
        dataMap5.put("name", "b");
        dataMap5.put("alias", "b");
        dataMap5.put("age", 18);
        dataList.add(dataMap5);
        Map<String, Object> dataMap6 = new LinkedHashMap<>();
        dataMap6.put("name", "b");
        dataMap6.put("alias", "a");
        dataMap6.put("age", 18);
        dataList.add(dataMap6);
        List<Map<String, Object>> aggList1 = aggData( ListUtil.toList("name", "alias"), ListUtil.toList("age"), dataList);
        List<List<Object>> aggList2 = aggData(2, convertData(dataList));
        System.out.println("");
    }
}
