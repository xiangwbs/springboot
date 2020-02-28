package com.xwbing.demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiangwb
 * @date 2019/6/3 19:08
 * 强大的表达式语言,可以存取对象的任意属性,遍历整个对象的结构图
 * 当有根据对象的某个不确定字段的值进行过滤的需求，可以考虑使用
 * {}代表数组{}代表数组。非root取值前加#
 */
@Slf4j
public class OgnlDemo {
    private static Map<String, Object> context = new HashMap<>();

    public static void main(String[] args) throws OgnlException {
        Use root = new Use();
        root.setAge(18);
        root.setName("test");
        root.setColor("red");
        JSONObject detail = new JSONObject();
        detail.put("detail", "in root");
        detail.put("no", 18);

        JSONArray contentArray = new JSONArray();
        JSONObject content1 = new JSONObject();
        JSONObject contentValue1 = new JSONObject();
        contentValue1.put("name", "test1");
        content1.put("value", contentValue1);

        JSONObject content2 = new JSONObject();
        JSONObject contentValue2 = new JSONObject();
        contentValue2.put("name", "test2");
        content2.put("value", contentValue2);

        contentArray.add(content1);
        contentArray.add(content2);

        detail.put("contents", contentArray);
        root.setDetail(detail);
        context.put("detail", "out root");
        boolean b1 = (boolean) Ognl.getValue("name == 'test'", context, root);
        boolean b2 = (boolean) Ognl.getValue("name != 'test'", context, root);
        boolean b3 = (boolean) Ognl.getValue("name in {'test','demo'}", context, root);
        boolean b4 = (boolean) Ognl.getValue("age in {17,19}", context, root);
        boolean b5 = (boolean) Ognl.getValue("detail.no >= 20", context, root);
        boolean b6 = (boolean) Ognl.getValue("detail.detail == 'in root'", context, root);
        boolean b7 = (boolean) Ognl.getValue("#detail == 'out root'", context, root);
        boolean b8 = (boolean) Ognl.getValue("name == 'test',age == 18", context, root);
        boolean b9 = (boolean) Ognl.getValue("name == 'test',age in {17,18}", context, root);
        Object name = Ognl.getValue("detail.contents.{value.name}", context, root);
        boolean b11 = (boolean) Ognl.getValue("detail.contents[0].value.name == 'test'", context, root);
        boolean b12 = (boolean) Ognl.getValue("detail.contents.{^ value.name == 'test'}.size()>0", context, root);
        detail = (JSONObject) Ognl.getValue("detail", context, root);
        //模糊过滤
        Map<String, Object> param = new HashMap<>();
        param.put("name", "te");
        param.put("color", "red,blue");
        boolean allMatch = param.entrySet().stream().allMatch(paramEntry -> {
            try {
                String original = String.valueOf(Ognl.getValue(paramEntry.getKey(), context, root));
                String value = String.valueOf(paramEntry.getValue());
                return Arrays.stream(value.split(",")).anyMatch(original::contains);
            } catch (OgnlException e) {
                return true;
            }
        });

        //推荐
        List<String> expressions = new ArrayList<>();
        expressions.add("name == 'test'");
        expressions.add("age in {17,18}");
        expressions.add("color like {'blue','red'}");
        expressions.add("detail.detail like 'in'");
        expressions.add("detail.contents[0].value.name like 'test'");
        expressions.add("detail.contents[0].value.name like {'test1','test2'}");
        expressions.add("detail.contents.{^ value.name == 'test1'}.size()>0");
        expressions.add("detail.contents.{value.name like 'test1'}");
        boolean match = match(expressions, root);
        System.out.println(match);
    }

    /**
     * 语法:field.field.field....+ 表达式。{}代表数组
     * f1 == 'xxx'
     * f1 != 'xxx'
     * f2 in {'xxx','xxx'}
     * f2 not in {'xxx','xxx'}
     * f3 > 18
     * f3 >= 18
     * f3 < 18
     * f3 <= 18
     * f1 == 'xxx',f3 > 18 //以上表达式可以任意组合,用逗号隔开
     * //like不是ognl原生的，不能组合
     * f4 like 'xxx'
     * f4 like {'xxx','xxx'}
     * *
     * 集合操作:collection.{Y XXX} 其中Y是一个选择操作符，XXX是选择用的逻辑表达式。对整个数组判断 表达式不能用数组
     * ? ：选择满足条件的所有元素
     * ^ ：选择满足条件的第一个元素
     * $ ：选择满足条件的最后一个元素
     * f5.f6.{^ f7.f8 == 'xxx'}.size()>0
     * *
     * 模糊过滤
     * f5.f6.{f7.f8 like 'xxx'}
     *
     * @param expressions
     * @param root
     * @return
     */
    private static boolean match(List<String> expressions, Object root) {
        return expressions.stream().allMatch(expression -> {
            try {
                if (expression.contains("like")) {//非ognl原生
                    String[] split = expression.split("\\s*like\\s*");
                    String field = split[0];
                    String value = split[1];
                    if (expression.contains(".{")) {//数组
                        field = field + "}";
                        value = value.split("}")[0];
                    }
                    return (boolean) Ognl.getValue("@com.xwbing.demo.OgnlDemo@like(" + field + "," + value + ")", context, root);
                } else {
                    return (boolean) Ognl.getValue(expression, context, root);
                }
            } catch (Exception e) {
                log.warn("ognl expression error:{}", e.getMessage());
                return true;
            }
        });

    }

    //ognl非原生模糊查询
    public static boolean like(String original, String value) {
        //数组
        if (original.contains("[")) {
            original = original.substring(original.indexOf("[") + 1, original.indexOf("]"));
            String finalValue = value;
            return Arrays.stream(original.split(", ")).anyMatch(s -> s.contains(finalValue));
        } else {
            if (value.contains("[")) {
                value = value.substring(value.indexOf("[") + 1, value.indexOf("]"));
                return Arrays.stream(value.split(", ")).anyMatch(original::contains);
            } else {
                return original.contains(value);
            }
        }
    }
}

@Data
class Use {
    private String name;
    private String color;
    private int age;
    private JSONObject detail;
}