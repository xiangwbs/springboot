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
                "  {\n" +
                "    \"年度\": 2024,\n" +
                "    \"项目类别\": \"基本支出\",\n" +
                "    \"全年预算数\": 19137901.94,\n" +
                "    \"决算数\": 19092134.55\n" +
                "  },\n" +
                "  {\n" +
                "    \"年度\": 2024,\n" +
                "    \"项目类别\": \"项目支出\",\n" +
                "    \"全年预算数\": 21295607,\n" +
                "    \"决算数\": 21145203.13\n" +
                "  },\n" +
                "  {\n" +
                "    \"年度\": 2022,\n" +
                "    \"项目类别\": \"基本支出\",\n" +
                "    \"全年预算数\": 12634142,\n" +
                "    \"决算数\": 12384193.79\n" +
                "  },\n" +
                "  {\n" +
                "    \"年度\": 2022,\n" +
                "    \"项目类别\": \"项目支出\",\n" +
                "    \"全年预算数\": 22170083.2,\n" +
                "    \"决算数\": 22028596.8\n" +
                "  },\n" +
                "  {\n" +
                "    \"年度\": 2021,\n" +
                "    \"项目类别\": \"基本支出\",\n" +
                "    \"全年预算数\": 13002586.1,\n" +
                "    \"决算数\": 12795512.77\n" +
                "  },\n" +
                "  {\n" +
                "    \"年度\": 2021,\n" +
                "    \"项目类别\": \"项目支出\",\n" +
                "    \"全年预算数\": 19777768,\n" +
                "    \"决算数\": 19535312.25\n" +
                "  }\n" +
                "]";
        JSONArray objects = JSONUtil.parseArray(s);
        String s1 = markdown(ListUtil.toList("年度","项目类别", "全年预算数", "决算数"), objects);
        System.out.println("");
    }

    private static String markdown(List<String> heads, JSONArray dataArray) {
        String headStr = String.join("|", heads);
        headStr = "|" + headStr + "|";
        String separator = IntStream
                .range(0, heads.size())
                .mapToObj(i -> "---")
                .collect(Collectors.joining("|"));
        separator = "|" + separator + "|";
        String datasStr = dataArray.stream().map(data -> {
            JSONObject dataObj = JSONUtil.parseObj(JSONUtil.toJsonStr(data));
            String dataStr = heads.stream().map(dataObj::getStr).collect(Collectors.joining("|"));
            return "|" + dataStr + "|";
        }).collect(Collectors.joining("\n"));
        return headStr + "\n" + separator + "\n" + datasStr;
    }
}
