package com.xwbing.controller.rest;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.domain.entity.vo.RestMessageVo;
import com.xwbing.service.pay.AliPayTradeService;
import com.xwbing.service.pay.AliPayTransferService;
import com.xwbing.service.pay.vo.AliPayTradeCreateParam;
import com.xwbing.service.pay.vo.AliPayTradeCreateResult;
import com.xwbing.service.pay.vo.AliPayTradePayParam;
import com.xwbing.service.pay.vo.AliPayTradePayResult;
import com.xwbing.service.pay.vo.AliPayTradeQueryResult;
import com.xwbing.service.pay.vo.AliPayTradeRefundParam;
import com.xwbing.service.pay.vo.AliPayTradeRefundResult;
import com.xwbing.service.pay.vo.AliPayWapPayParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年07月24日 下午5:23
 */
@Slf4j
@Api(tags = "AliPayController", description = "支付宝接口")
@RestController
@RequestMapping("aliPay")
public class AliPayController {
    @Resource
    private AliPayTradeService aliPayTradeService;
    @Resource
    private AliPayTransferService aliPayTransferService;

    @ApiOperation(value = "查询支付宝账户余额", response = RestMessageVo.class)
    @PostMapping("accountQuery")
    public BigDecimal accountQuery() {
        return aliPayTransferService.accountQuery();
    }

    @ApiOperation(value = "交易创建", response = RestMessageVo.class)
    @PostMapping("tradeCreate")
    public AliPayTradeCreateResult tradePayNotify(@RequestBody AliPayTradeCreateParam param) {
        return aliPayTradeService.tradeCreate(param);
    }

    @ApiOperation(value = "当面付", response = RestMessageVo.class)
    @PostMapping("tradePay")
    public AliPayTradePayResult tradePayNotify(@RequestBody AliPayTradePayParam param) {
        return aliPayTradeService.tradePay(param);
    }

    @ApiOperation(value = "手机网站支付", response = RestMessageVo.class)
    @PostMapping("wapPay")
    public void tradePayNotify(HttpServletResponse httpResponse, @RequestBody AliPayWapPayParam param) {
        aliPayTradeService.wapPay(httpResponse, param);
    }

    @ApiOperation(value = "交易查询", response = RestMessageVo.class)
    @GetMapping("tradeQuery")
    public AliPayTradeQueryResult tradeQuery(String outTradeNo, String tradeNo) {
        return aliPayTradeService.tradeQuery(outTradeNo, tradeNo);
    }

    @ApiOperation(value = "退款", response = RestMessageVo.class)
    @PostMapping("tradeRefund")
    public AliPayTradeRefundResult tradeRefund(@RequestBody AliPayTradeRefundParam param) {
        return aliPayTradeService.tradeRefund(param);
    }
}
