package com.xwbing.service.demo.DesignPattern.strategy;

import org.springframework.stereotype.Service;

/**
 * @author xiangwb
 * @date 2020/3/6 18:16
 */
@Service
public class DWxPayStrategy implements DPayStrategy {
    @Override
    public String toPay() {
        return "调用微信接口";
    }
}
