package com.xwbing.service.demo.ratelimit.Sentinel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import org.springframework.util.StopWatch;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

/**
 * 基于滑动窗口
 */
public class SentinelRateLimitDemo {
    {
        initFlowRules();
    }

    public static void main(String[] args) {
        SentinelRateLimitDemo se = new SentinelRateLimitDemo();
        se.doRequest();
    }

    public void doRequest() {
        StopWatch sw = new StopWatch();
        sw.start();
        Random random = new Random();
        IntStream.range(1, 100).forEach(value -> {
            try (Entry entry = SphU.entry("doRequest")) {
                System.out.println(Thread.currentThread().getName() + ":+执行业务逻辑");
                Thread.sleep(random.nextInt(500));
            } catch (BlockException e) {
                //如果被限流，就会抛出BlockedException
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        sw.stop();
        System.out.println(sw.getTotalTimeSeconds());
    }

    private void initFlowRules() {
        // 限流的规则
        FlowRule rule = new FlowRule();
        rule.setCount(5);
        // 针对QPS限流
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 被保护的资源
        rule.setResource("doRequest");
        List<FlowRule> rules = new ArrayList<>();
        rules.add(rule);
        // 让Sentinel加载限流规则
        FlowRuleManager.loadRules(rules);
    }
}
