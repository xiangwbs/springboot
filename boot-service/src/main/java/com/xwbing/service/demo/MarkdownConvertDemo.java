package com.xwbing.service.demo;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author daofeng
 * @version $
 * @since 2025年10月21日 14:48
 */
public class MarkdownConvertDemo {
    public static void main(String[] args) {
        String s = "[\n" +
                "    {\n" +
                "      \"年度\": 2020,\n" +
                "      \"单位代码\": 407007,\n" +
                "      \"单位名称\": \"杭州博物馆（杭州博物院（筹））\",\n" +
                "      \"全年预算数\": 7787557,\n" +
                "      \"决算数\": 7080222.72\n" +
                "    },\n" +
                "    {\n" +
                "      \"年度\": 2021,\n" +
                "      \"单位代码\": 407007,\n" +
                "      \"单位名称\": \"杭州博物馆（杭州博物院（筹））\",\n" +
                "      \"全年预算数\": 32780354.1,\n" +
                "      \"决算数\": 32330825.02\n" +
                "    },\n" +
                "    {\n" +
                "      \"年度\": 2022,\n" +
                "      \"单位代码\": 407007,\n" +
                "      \"单位名称\": \"杭州博物馆（杭州博物院（筹））\",\n" +
                "      \"全年预算数\": 34804225.2,\n" +
                "      \"决算数\": 34412790.59\n" +
                "    },\n" +
                "    {\n" +
                "      \"年度\": 2023,\n" +
                "      \"单位代码\": 407007,\n" +
                "      \"单位名称\": \"杭州博物馆（杭州博物院（筹））\",\n" +
                "      \"全年预算数\": 40526032.25,\n" +
                "      \"决算数\": 37858800.13\n" +
                "    },\n" +
                "    {\n" +
                "      \"年度\": 2024,\n" +
                "      \"单位代码\": 407007,\n" +
                "      \"单位名称\": \"杭州博物馆（杭州博物院（筹））\",\n" +
                "      \"全年预算数\": 40433508.94,\n" +
                "      \"决算数\": 40237337.68\n" +
                "    }\n" +
                "  ]";
        JSONArray objects = JSONUtil.parseArray(s);
        String s1 = markdownExcel(ListUtil.toList("年度", "全年预算数", "决算数"), objects);
        System.out.println("");
    }

    private static String markdownExcel(List<String> heads, JSONArray dataArray) {
        String head = String.join("|", heads);
        head = "| " + head + " |";
        String separator = IntStream
                .range(0, heads.size())
                .mapToObj(i -> "---")
                .collect(Collectors.joining(" | "));
        separator = "| " + separator + " |";
        String datasStr = dataArray.stream().map(data -> {
            JSONObject dataObj = JSONUtil.parseObj(JSONUtil.toJsonStr(data));
            String dataStr = heads.stream().map(dataObj::getStr).collect(Collectors.joining(" | "));
            return "| " + dataStr + " |";
        }).collect(Collectors.joining("\n"));
        return head + "\n" + separator + "\n" + datasStr;
    }
}
