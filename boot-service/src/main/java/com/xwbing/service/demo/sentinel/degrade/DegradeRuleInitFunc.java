package com.xwbing.service.demo.sentinel.degrade;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;

public class DegradeRuleInitFunc implements InitFunc {
    @Override
    public void init() {
        initDegrade();
    }

    private void initDegrade() {
        List<DegradeRule> degradeRules = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        // 熔断策略（慢调用比例、异常比例、异常数）
        // 慢调用比例
        // rule.setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType());
        // 慢调用比例阈值
        // rule.setSlowRatioThreshold(0.2);
        // 异常比例
        // rule.setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType());
        // 异常数量
        rule.setGrade(CircuitBreakerStrategy.ERROR_COUNT.getType());
        // 异常比例、异常数量、慢调用最大响应时间（毫秒）
        rule.setCount(3);
        // 熔断时长（秒）
        rule.setTimeWindow(10);
        // 熔断触发的最小请求数
        rule.setMinRequestAmount(5);
        // 统计时长（秒）
        rule.setStatIntervalMs(1000);
        rule.setResource("testDegrade");
        degradeRules.add(rule);

        DegradeRuleManager.loadRules(degradeRules);
    }
}