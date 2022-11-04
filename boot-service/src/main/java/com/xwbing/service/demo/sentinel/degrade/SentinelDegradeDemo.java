package com.xwbing.service.demo.sentinel.degrade;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreaker.State;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.EventObserverRegistry;

public class SentinelDegradeDemo {
    public static void main(String[] args) {
        initDegrade();
        for (int i = 0; i < 1000; i++) {
            try (Entry entry = SphU.entry("myResource")) {
                System.out.println("业务操作成功");
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(10, 100));
            } catch (BlockException ex) {
                ex.printStackTrace();
                System.out.println("业务操作失败");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initDegrade() {
        List<DegradeRule> degradeRules = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        // 慢调用比例
        rule.setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType());
        // 慢调用最大响应时间（毫秒）
        rule.setCount(20);
        // 熔断时间s
        rule.setTimeWindow(10);
        // 慢调用比例阈值
        rule.setSlowRatioThreshold(0.2);
        // 熔断触发的最小请求数
        rule.setMinRequestAmount(10);
        // 统计时长s
        rule.setStatIntervalMs(1000);
        rule.setResource("myResource");
        degradeRules.add(rule);
        DegradeRuleManager.loadRules(degradeRules);

        // 熔断器事件监听
        EventObserverRegistry.getInstance()
                .addStateChangeObserver("log", (prevState, newState, degradeRule, snapshotValue) -> {
                    if (newState == State.OPEN) {
                        System.out.println(prevState.name() + ": open" + snapshotValue);
                    } else {
                        System.out.println(prevState.name() + ": other" + snapshotValue);
                    }
                });
    }
}