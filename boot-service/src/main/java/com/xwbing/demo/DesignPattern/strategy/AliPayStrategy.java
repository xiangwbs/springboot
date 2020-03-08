package com.xwbing.demo.DesignPattern.strategy;

import org.springframework.stereotype.Service;

/**
 * @author xiangwb
 * @date 2020/3/6 18:15
 */
@Service
public class AliPayStrategy implements PayStrategy {
    @Override
    public String toPay() {
        return "调用支付宝接口";
    }
}
