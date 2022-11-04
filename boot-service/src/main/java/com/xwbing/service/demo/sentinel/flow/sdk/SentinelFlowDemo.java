package com.xwbing.service.demo.sentinel.flow.sdk;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

/**
 * 基于滑动窗口
 */
public class SentinelFlowDemo {
    public static void main(String[] args) {
        initFlowRules();
        while (true) {
            try (Entry entry = SphU.entry("myResource")) {
                System.out.println("业务资源访问成功！");
            } catch (BlockException ex) {
                ex.printStackTrace();
                System.out.println("资源访问失败！！！");
            }
        }
    }

    private static void initFlowRules() {
        // 限流的规则
        FlowRule rule = new FlowRule();
        rule.setCount(5);
        // 针对QPS限流
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 被保护的资源
        rule.setResource("myResource");
        List<FlowRule> rules = new ArrayList<>();
        rules.add(rule);
        // 让sentinel加载限流规则
        FlowRuleManager.loadRules(rules);
    }
}