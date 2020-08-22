package com.xwbing.service;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;

import com.xwbing.BaseTest;
import com.xwbing.service.pay.AliPayService;

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
