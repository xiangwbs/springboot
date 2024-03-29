package com.xwbing.service.demo.designpattern.strategy;

import org.springframework.stereotype.Service;

/**
 * @author xiangwb
 * @date 2020/3/6 18:15
 */
@Service
public class DAliPayStrategy implements DPayStrategy {
    @Override
    public String toPay() {
        return "调用支付宝接口";
    }
}
