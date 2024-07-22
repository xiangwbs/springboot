package com.xwbing.service.demo;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
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
        if (CollectionUtils.isEmpty(dimensionList) || CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
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
                    for (int i = 0; i < dimensionList.size(); i++) {
                        String dimension = dimensionArray[i];
                        if (StrUtil.isNullOrUndefined(dimension)) {
                            dimension = "";
                        }
                        dataMap.put(dimensionList.get(i), dimension);
                    }
                    if (CollectionUtils.isNotEmpty(metricList)) {
                        List<Map<String, Object>> metricDataList = dataListMap.getOrDefault(groupDimension, Collections.emptyList());
                        metricList.forEach(metric -> {
                                    double sum = metricDataList.stream()
                                            .mapToDouble(value -> Optional.ofNullable(value.get(metric))
                                                    .map(o -> Double.parseDouble(String.valueOf(o)))
                                                    .orElse(0.0))
                                            .sum();
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
        if (dimensionCount == null || dimensionCount <= 0 || CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        List<String> dimensionList = dataList.stream()
                .map(data -> groupDimensionData(dimensionCount, data))
                .distinct()
                .collect(Collectors.toList());
        Map<String, List<List<Object>>> metricMap = dataList.stream()
                .collect(Collectors.groupingBy((data -> groupDimensionData(dimensionCount, data))));
        int size = dataList.get(0).size();
        return dimensionList.stream().map(dimension -> {
            List<Object> list = Arrays.stream(dimension.split("-")).map(d -> {
                if (StrUtil.isNullOrUndefined(d)) {
                    d = "";
                }
                return d;
            }).collect(Collectors.toList());
            List<List<Object>> metricList = metricMap.get(dimension);
            for (int i = dimensionCount; i < size; i++) {
                int finalI = i;
                double sum = metricList.stream()
                        .mapToDouble(obj -> Optional.ofNullable(obj.get(finalI))
                                .map(o -> Double.parseDouble(String.valueOf(o)))
                                .orElse(0.0))
                        .sum();
                list.add(sum);
            }
            return list;
        }).collect(Collectors.toList());
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
                .map(dimension -> {
                    String dimensionStr = String.valueOf(data.get(dimension));
                    if (StrUtil.isNullOrUndefined(dimensionStr)) {
                        // 没有数据为null字符串，避免split("-")缺少数据
                        dimensionStr = "null";
                    }
                    return dimensionStr;
                })
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
                .map(dimension -> {
                    String dimensionStr = String.valueOf(dimension);
                    if (StrUtil.isNullOrUndefined(dimensionStr)) {
                        // 没有数据为null字符串，避免split("-")缺少数据
                        dimensionStr = "null";
                    }
                    return dimensionStr;
                })
                .collect(Collectors.joining("-"));
    }

    private static List<List<Object>> convertData(List<Map<String, Object>> dataList) {
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
        dataMap3.put("alias", null);
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
        dataMap5.put("age", null);
        dataList.add(dataMap5);
        Map<String, Object> dataMap6 = new LinkedHashMap<>();
        dataMap6.put("name", "b");
        dataMap6.put("alias", "a");
        dataMap6.put("age", 18);
        dataList.add(dataMap6);
        List<Map<String, Object>> aggList1 = aggData(ListUtil.toList("name", "alias"), ListUtil.toList("age"), dataList);
        List<List<Object>> aggList2 = aggData(2, convertData(dataList));
        System.out.println("");
    }
}
