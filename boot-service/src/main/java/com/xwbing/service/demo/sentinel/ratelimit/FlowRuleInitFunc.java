package com.xwbing.service.demo.sentinel.ratelimit;

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
        // 限流阈值
        rule1.setCount(2);
        // 限流阈值类型（基于QPS/并发数的流量控制）
        // RuleConstant.FLOW_GRADE_QPS  qps
        // RuleConstant.FLOW_GRADE_THREAD 并发线程数
        rule1.setGrade(RuleConstant.FLOW_GRADE_QPS);

        // 限流策略（基于调用关系的流量控制）
        // 根据调用方限流
        rule1.setStrategy(RuleConstant.STRATEGY_DIRECT);
        // default 不区分调用者，任何调用者的请求都会进行流量统计
        // xxx 针对某个特定的调用者，只有这个调用者的请求才会进行流量控制
        // other 表示针对除了xxx以外的其他调用方的流量进行流量控制
        rule1.setLimitApp("default");
        // 具有关系的资源流量控制
        // rule1.setStrategy(RuleConstant.STRATEGY_RELATE);
        // 根据调用链路入口限流
        // rule1.setStrategy(RuleConstant.STRATEGY_CHAIN);

        // 流量控制
        // 直接拒绝
        rule1.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT);
        // 冷启动
        // rule1.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP);
        // rule1.setWarmUpPeriodSec(60);
        // 匀速排队
        // rule1.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
        // rule1.setMaxQueueingTimeMs(500);

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