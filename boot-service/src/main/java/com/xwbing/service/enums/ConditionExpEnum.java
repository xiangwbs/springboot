package com.xwbing.service.enums;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
    BE_EQUAL_TO("等于"),
    NOT_BE_EQUAL_TO("不等于"),
    GREATER_THAN("大于"),
    LESS_THAN("小于"),
    GREATER_THAN_OR_EQUAL_TO("大于等于"),
    LESS_THAN_OR_EQUAL_TO("小于等于"),
    CONTAIN("包含"),
    DOES_NOT_CONTAIN("不包含"),
    IS_NULL("为空"),
    IS_NOT_NULL("不为空");

    private final String name;

    public static List<Rule> doMatch(List<List<Rule>> ruleGroups, Map<String, String> dataMap) {
        // 匹配成功的数据，用于记录
        List<Rule> matchRuleList = new ArrayList<>();
        // 这一层为【或】，任意组匹配就算匹配成功
        for (List<Rule> ruleGroup : ruleGroups) {
            if (CollUtil.isEmpty(ruleGroup)) {
                break;
            }
            matchRuleList.clear();
            boolean matched = false;
            // 条件组内为【且】
            for (Rule rule : ruleGroup) {
                // 获取数据
                String data = dataMap.get(rule.getName());
                // 获取不到匹配项视作匹配失败
                matched = StringUtils.isNotEmpty(data) && match(rule, data);
                if (matched) {
                    matchRuleList.add(rule);
                } else {
                    // 任意匹配失败则退出当前循环，开始匹配下个组
                    break;
                }
            }
            // 如果当前组匹配成功则退出
            if (matched) {
                break;
            }
        }
        return matchRuleList;
    }

    public static boolean match(Rule rule, String data) {
        return StrUtil.isNumeric(data) ?
                ConditionExpEnum.numberRule(Long.parseLong(data), rule.getConditionValue(), rule.getConditionExp().getName()) :
                ConditionExpEnum.stringRule(data, rule.getConditionValue(), rule.getConditionExp().getName());
    }

    private static boolean numberRule(long value, String conditionValue, String conditionExp) {
        List<Long> numbers;
        if (JSONUtil.isTypeJSONArray(conditionValue)) {
            numbers = JSONUtil.toList(conditionValue, Long.class);
        } else {
            numbers = ListUtil.toList(Long.valueOf(conditionValue));
        }
        if (numbers.size() == 1) {
            if (BE_EQUAL_TO.getName().equals(conditionExp)) {
                return value == numbers.get(0);
            } else if (NOT_BE_EQUAL_TO.getName().equals(conditionExp)) {
                return value != numbers.get(0);
            } else if (GREATER_THAN.getName().equals(conditionExp)) {
                return value > numbers.get(0);
            } else if (LESS_THAN.getName().equals(conditionExp)) {
                return value < numbers.get(0);
            } else if (GREATER_THAN_OR_EQUAL_TO.getName().equals(conditionExp)) {
                return value >= numbers.get(0);
            } else if (LESS_THAN_OR_EQUAL_TO.getName().equals(conditionExp)) {
                return value <= numbers.get(0);
            }
        } else {
            if (CONTAIN.getName().equals(conditionExp)) {
                return numbers.contains(value);
            } else if (DOES_NOT_CONTAIN.getName().equals(conditionExp)) {
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
            if (BE_EQUAL_TO.getName().equals(conditionExp)) {
                return value.equals(strings.get(0));
            } else if (NOT_BE_EQUAL_TO.getName().equals(conditionExp)) {
                return !value.equals(strings.get(0));
            } else if (IS_NULL.getName().equals(conditionExp)) {
                return StringUtils.isEmpty(value);
            } else if (IS_NOT_NULL.getName().equals(conditionExp)) {
                return StringUtils.isNotEmpty(value);
            } else if (CONTAIN.getName().equals(conditionExp)) {
                return conditionValue.contains(value);
            } else if (DOES_NOT_CONTAIN.getName().equals(conditionExp)) {
                return !conditionValue.contains(value);
            }
        } else {
            if (CONTAIN.getName().equals(conditionExp)) {
                return conditionValue.contains(value);
            } else if (DOES_NOT_CONTAIN.getName().equals(conditionExp)) {
                return !conditionValue.contains(value);
            }
        }
        return false;
    }

    public static List<String> listConditionExp() {
        return Arrays.stream(ConditionExpEnum.values()).map(ConditionExpEnum::getName).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        boolean b = numberRule(100, "[\"50\",\"100\"]", "包含");
        System.out.println(b);
    }

    @Data
    public static class Rule {
        private String name;
        private String conditionValue;
        private ConditionExpEnum conditionExp;
    }
}