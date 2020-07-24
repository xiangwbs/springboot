package com.xwbing.service;

import javax.annotation.Resource;

import org.junit.Test;

import com.xwbing.BaseTest;
import com.xwbing.service.pay.AliPayTradeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PayServiceTest extends BaseTest {
    @Resource
    private AliPayTradeService aliPayTradeService;

    @Test
    public void queryBillDownloadUrl() {
        aliPayTradeService.queryBillDownloadUrl("2020-06-30");
    }
}
