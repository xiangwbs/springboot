package com.xwbing.controller.rest;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.annotation.LogInfo;
import com.xwbing.domain.entity.pay.alipay.AliPayTradePayNotifyRequest;
import com.xwbing.domain.entity.vo.RestMessageVo;
import com.xwbing.service.rest.AliPayNotifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xiangwb
 * @date 20/2/9 15:59
 */
@Slf4j
@Api(tags = "aliPayNotifyController", description = "支付宝异步通知")
@RestController
@RequestMapping("aliPayNotice")
public class AliPayNotifyController {
    @Resource
    private AliPayNotifyService aliPayNotifyService;

    @LogInfo("支付宝统一收单交易支付异步通知")
    @ApiOperation(value = "支付宝统一收单交易支付异步通知", response = RestMessageVo.class)
    @PostMapping("tradePay")
    public void tradePayNotify(AliPayTradePayNotifyRequest alipayTradePayNotifyRequest, HttpServletResponse response) throws IOException {
        log.info("aliPayTradePayNotify:{}", JSONObject.toJSONString(alipayTradePayNotifyRequest));
        //验签
        aliPayNotifyService.verifyTradePayParam(alipayTradePayNotifyRequest);
        //业务处理
        aliPayNotifyService.generalTradePay(alipayTradePayNotifyRequest);
        //返回值
        response.setContentType("charset=UTF-8");
        response.getWriter().write("success");
    }
}
