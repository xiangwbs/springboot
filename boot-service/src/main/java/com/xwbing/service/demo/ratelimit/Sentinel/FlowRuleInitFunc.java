package com.xwbing.service.demo.ratelimit.Sentinel;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

public class FlowRuleInitFunc implements InitFunc {
    @Override
    public void init() {
        List<FlowRule> rules = new ArrayList<>();
        // 限流的规则
        FlowRule rule1 = new FlowRule();
        rule1.setCount(2);
        // 针对QPS限流
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 被保护的资源
        rule1.setResource("testBlock");
        rules.add(rule1);

        FlowRule rule2 = new FlowRule();
        rule2.setCount(2);
        rule2.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule2.setResource("testFallback");
        rules.add(rule2);
        // 让Sentinel加载限流规则
        FlowRuleManager.loadRules(rules);
    }
}