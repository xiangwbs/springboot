package com.xwbing.service.enums;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
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

    public static List<String> listConditionExp() {
        return Arrays.stream(ConditionExpEnum.values()).map(ConditionExpEnum::getName).collect(Collectors.toList());
    }

    /**
     * @param ruleGroups dataMap里的数据 一组规则可以包含多个条件
     * @param dataMap
     * @return
     */
    public static List<Condition> match(List<List<Condition>> ruleGroups, Map<String, String> dataMap) {
        // 是否匹配成功标记
        boolean matched = false;
        // 匹配成功的数据，用于记录
        List<Condition> matchList = new ArrayList<>();
        // 这一层为【或】，任意组匹配就算匹配成功
        for (List<Condition> ruleGroup : ruleGroups) {
            if (CollectionUtils.isEmpty(ruleGroup)) {
                break;
            }
            matchList.clear();
            // 条件组内为【且】
            for (Condition condition : ruleGroup) {
                // 获取数据
                String data = dataMap.get(condition.getName());
                // 获取不到匹配项视作匹配失败
                matched = StringUtils.isNotEmpty(data) && rule(condition, data);
                if (matched) {
                    matchList.add(condition);
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
        if (matched) {
            return matchList;
        } else {
            return Collections.emptyList();
        }
    }

    public static boolean rule(Condition condition, String data) {
        return StrUtil.isNumeric(data) ?
                ConditionExpEnum.numberRule(Long.parseLong(data), condition.getConditionValue(), condition.getConditionExp().getName()) :
                ConditionExpEnum.stringRule(data, condition.getConditionValue(), condition.getConditionExp().getName());
    }

    private static boolean numberRule(long value, String conditionValue, String conditionExp) {
        List<Long> numbers;
        if (JSONUtil.isTypeJSONArray(conditionValue)) {
            numbers = JSONUtil.toList(conditionValue, Long.class);
        } else {
            numbers = ListUtil.toList(Long.valueOf(conditionValue));
        }
        if (numbers.size() == 1) {
            if (EQ.getName().equals(conditionExp)) {
                return value == numbers.get(0);
            } else if (NE.getName().equals(conditionExp)) {
                return value != numbers.get(0);
            } else if (GT.getName().equals(conditionExp)) {
                return value > numbers.get(0);
            } else if (GE.getName().equals(conditionExp)) {
                return value >= numbers.get(0);
            } else if (LT.getName().equals(conditionExp)) {
                return value < numbers.get(0);
            } else if (LE.getName().equals(conditionExp)) {
                return value <= numbers.get(0);
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
                return value.equals(conditionValue);
            } else if (NE.getName().equals(conditionExp)) {
                return !value.equals(conditionValue);
            } else if (IS_NULL.getName().equals(conditionExp)) {
                return StringUtils.isEmpty(value);
            } else if (IS_NOT_NULL.getName().equals(conditionExp)) {
                return StringUtils.isNotEmpty(value);
            } else if (IN.getName().equals(conditionExp)) {
                return value.contains(conditionValue);
            } else if (NOT_IN.getName().equals(conditionExp)) {
                return !value.contains(conditionValue);
            }
        } else {
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
        ruleGroups.add(ListUtil.toList(Condition.builder().name("城市").conditionValue("[\"杭州\",\"上海\"]").conditionExp(ConditionExpEnum.IN).build()));
        ruleGroups.add(ListUtil.toList(Condition.builder().name("排队数").conditionValue("10").conditionExp(ConditionExpEnum.GT).build(), Condition.builder().name("小休数").conditionValue("10").conditionExp(ConditionExpEnum.GT).build()));
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("排队数", "15");
        dataMap.put("小休数", "20");
        dataMap.put("城市", "杭州城市");
        List<Condition> matchList = match(ruleGroups, dataMap);
        if (CollectionUtils.isNotEmpty(matchList)) {
            String reason = matchList
                    .stream()
                    .map(condition -> condition.getName() + condition.getConditionExp().getName() + "设定值" + condition.getConditionValue())
                    .collect(Collectors.joining(","));
            System.out.println(reason);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Condition {
        private String name;
        private String conditionValue;
        private ConditionExpEnum conditionExp;
    }
}