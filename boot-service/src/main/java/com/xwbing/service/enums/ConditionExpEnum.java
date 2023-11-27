package com.xwbing.service.enums;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 条件表达式
 *
 * @author daofeng
 * @version $
 * @since 2019/7/5 16:16
 */
@Getter
@AllArgsConstructor
public enum ConditionExpEnum {
    EQ("等于"),
    NE("不等于"),
    GT("大于"),
    GE("大于等于"),
    LT("小于"),
    LE("小于等于"),
    IN("包含"),
    NOT_IN("不包含"),
    IS_NULL("为空"),
    IS_NOT_NULL("不为空");

    private final String name;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Condition {
        // 条件名称
        private String name;
        // 条件key
        private String key;
        // 条件值
        private String value;
        // 条件表达式
        private ConditionExpEnum expression;
    }

    public static List<String> listConditionExp() {
        return Arrays.stream(ConditionExpEnum.values()).map(ConditionExpEnum::getName).collect(Collectors.toList());
    }

    /**
     * @param ruleGroups 规则组 一组规则可以包含多个条件
     * @param dataMap    key:条件Key value:用户数据
     * @return 匹配的规则组
     */
    public static List<Condition> match(List<List<Condition>> ruleGroups, Map<String, String> dataMap) {
        if (CollectionUtils.isEmpty(ruleGroups) || MapUtils.isEmpty(dataMap)) {
            return Collections.emptyList();
        }
        boolean matched = false;
        // 这一层为【或】，任意组匹配就算匹配成功
        for (List<Condition> ruleGroup : ruleGroups) {
            if (CollectionUtils.isEmpty(ruleGroup)) {
                continue;
            }
            // 条件组内为【且】
            for (Condition rule : ruleGroup) {
                // 获取数据
                String data = dataMap.get(rule.getKey());
                // 匹配规则
                matched = rule(rule, data);
                if (!matched) {
                    // 任意匹配失败则退出当前循环，开始匹配下个组
                    break;
                }
            }
            // 如果当前组匹配成功则返回对应条件
            if (matched) {
                return ruleGroup;
            }
        }
        return Collections.emptyList();
    }

    private static boolean rule(Condition condition, String data) {
        return StrUtil.isNumeric(data)
                ? ConditionExpEnum.numberRule(Long.parseLong(data), condition.getValue(), condition.getExpression().getName())
                : ConditionExpEnum.stringRule(data, condition.getValue(), condition.getExpression().getName());
    }

    private static boolean numberRule(Long value, String conditionValue, String conditionExp) {
        List<Long> numbers;
        if (JSONUtil.isTypeJSONArray(conditionValue)) {
            numbers = JSONUtil.toList(conditionValue, Long.class);
        } else {
            numbers = ListUtil.toList(Long.valueOf(conditionValue));
        }
        if (numbers.size() == 1) {
            Long number = numbers.get(0);
            if (EQ.getName().equals(conditionExp)) {
                return Objects.equals(value, number);
            } else if (NE.getName().equals(conditionExp)) {
                return !Objects.equals(value, number);
            } else if (GT.getName().equals(conditionExp)) {
                return value > number;
            } else if (GE.getName().equals(conditionExp)) {
                return value >= number;
            } else if (LT.getName().equals(conditionExp)) {
                return value < number;
            } else if (LE.getName().equals(conditionExp)) {
                return value <= number;
            }
        } else {
            if (IN.getName().equals(conditionExp)) {
                return numbers.contains(value);
            } else if (NOT_IN.getName().equals(conditionExp)) {
                return !numbers.contains(value);
            }
        }
        return false;
    }

    private static boolean stringRule(String value, String conditionValue, String conditionExp) {
        List<String> strings;
        if (JSONUtil.isTypeJSONArray(conditionValue)) {
            strings = JSONUtil.toList(conditionValue, String.class);
        } else {
            strings = ListUtil.toList(conditionValue);
        }
        if (strings.size() == 1) {
            if (EQ.getName().equals(conditionExp)) {
                return StringUtils.isNotEmpty(value) && value.equals(conditionValue);
            } else if (NE.getName().equals(conditionExp)) {
                return StringUtils.isNotEmpty(value) && !value.equals(conditionValue);
            } else if (IS_NULL.getName().equals(conditionExp)) {
                return StringUtils.isEmpty(value);
            } else if (IS_NOT_NULL.getName().equals(conditionExp)) {
                return StringUtils.isNotEmpty(value);
            } else if (IN.getName().equals(conditionExp)) {
                return StringUtils.isNotEmpty(value) && value.contains(conditionValue);
            } else if (NOT_IN.getName().equals(conditionExp)) {
                return StringUtils.isNotEmpty(value) && !value.contains(conditionValue);
            }
        } else {
            if (StringUtils.isEmpty(value)) {
                return false;
            }
            if (IN.getName().equals(conditionExp)) {
                return strings.stream().anyMatch(value::contains);
            } else if (NOT_IN.getName().equals(conditionExp)) {
                return strings.stream().noneMatch(value::contains);
            }
        }
        return false;
    }

    public static void main(String[] args) {
        List<List<Condition>> ruleGroups = new ArrayList<>();
//        ruleGroups.add(ListUtil.toList(Condition.builder().name("名字").key("name").expression(ConditionExpEnum.IS_NOT_NULL).build()));
        ruleGroups.add(ListUtil.toList(Condition.builder().name("地址").key("address").value("[\"杭州\",\"上海\"]").expression(ConditionExpEnum.IN).build()));
        ruleGroups.add(ListUtil.toList(Condition.builder().name("排队数").key("wait").value("10").expression(ConditionExpEnum.GT).build(), Condition.builder().name("小休数").key("rest").value("10").expression(ConditionExpEnum.GT).build()));
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("wait", "15");
        dataMap.put("rest", "20");
        dataMap.put("address", "大杭州");
        dataMap.put("name", "道风");
        List<Condition> matchList = match(ruleGroups, dataMap);
        if (CollectionUtils.isNotEmpty(matchList)) {
            String reason = matchList
                    .stream()
                    .map(condition -> {
                        String content = condition.getName() + condition.getExpression().getName();
                        if (StringUtils.isNotEmpty(condition.getValue())) {
                            content += condition.getValue();
                        }
                        return content;
                    })
                    .collect(Collectors.joining(";"));
            System.out.println(reason);
        }
    }
}