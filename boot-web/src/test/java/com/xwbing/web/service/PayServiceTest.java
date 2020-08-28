package com.xwbing.web.service;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;

import com.xwbing.web.BaseTest;
import com.xwbing.starter.alipay.AliPayService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PayServiceTest extends BaseTest {
    @Resource
    private AliPayService aliPayTradeService;

    @Test
    public void queryBillDownloadUrl() {
        aliPayTradeService.queryBillDownloadUrl("2020-06-30");
    }
}
