package com.xwbing.web.controller.rest;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.starter.aspect.annotation.Limit;
import com.xwbing.starter.clusterseq.ClusterSeqGenerator;
import com.xwbing.service.domain.entity.vo.RestMessageVo;
import com.xwbing.starter.alipay.AliPayService;
import com.xwbing.service.service.pay.TransferService;
import com.xwbing.starter.alipay.vo.request.AliPayAppPayParam;
import com.xwbing.starter.alipay.vo.response.AliPayAppPayResult;
import com.xwbing.starter.alipay.vo.request.AliPayPagePayParam;
import com.xwbing.starter.alipay.vo.response.AliPayRefundQueryResult;
import com.xwbing.starter.alipay.vo.response.AliPayTradeCloseResult;
import com.xwbing.starter.alipay.vo.request.AliPayTradeCreateParam;
import com.xwbing.starter.alipay.vo.response.AliPayTradeCreateResult;
import com.xwbing.starter.alipay.vo.request.AliPayTradePayParam;
import com.xwbing.starter.alipay.vo.response.AliPayTradePayResult;
import com.xwbing.starter.alipay.vo.request.AliPayTradePreCreateParam;
import com.xwbing.starter.alipay.vo.response.AliPayTradePreCreateResult;
import com.xwbing.starter.alipay.vo.response.AliPayTradeQueryResult;
import com.xwbing.starter.alipay.vo.request.AliPayTradeRefundParam;
import com.xwbing.starter.alipay.vo.response.AliPayTradeRefundResult;
import com.xwbing.starter.alipay.vo.request.AliPayTransferParam;
import com.xwbing.starter.alipay.vo.request.AliPayWapPayParam;

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
    private AliPayService aliPayService;
    @Resource
    private TransferService transferService;
    @Resource
    private ClusterSeqGenerator clusterSeqGenerator;

    @ApiOperation(value = "查询支付宝账户余额", response = RestMessageVo.class)
    @PostMapping("accountQuery")
    public BigDecimal accountQuery() {
        return aliPayService.accountQuery();
    }

    @ApiOperation(value = "转账", response = RestMessageVo.class)
    @PostMapping("doTransfer")
    public void doTransfer(@RequestBody AliPayTransferParam param) {
        String orderNo = clusterSeqGenerator.getSeqId("orderNo") + "";
        param = param.toBuilder().outBizNo(orderNo).build();
        transferService.doTransfer(param);
    }

    @Limit("#p0.getOutTradeNo()")
    @ApiOperation(value = "交易创建", response = RestMessageVo.class)
    @PostMapping("tradeCreate")
    public AliPayTradeCreateResult tradePayNotify(@RequestBody AliPayTradeCreateParam param) {
        return aliPayService.tradeCreate(param);
    }

    @ApiOperation(value = "交易关闭", response = RestMessageVo.class)
    @PostMapping("tradeClose")
    public AliPayTradeCloseResult tradeClose(String outTradeNo, String tradeNo) {
        return aliPayService.tradeClose(outTradeNo, tradeNo);
    }

    @ApiOperation(value = "条码支付", response = RestMessageVo.class)
    @PostMapping("tradePay")
    public AliPayTradePayResult tradePayNotify(@RequestBody AliPayTradePayParam param) {
        return aliPayService.tradePay(param);
    }

    @ApiOperation(value = "扫码支付", response = RestMessageVo.class)
    @PostMapping("tradePreCreate")
    public AliPayTradePreCreateResult tradePreCreate(@RequestBody AliPayTradePreCreateParam param) {
        return aliPayService.tradePreCreate(param);
    }

    @ApiOperation(value = "app支付", response = RestMessageVo.class)
    @PostMapping("appPay")
    public AliPayAppPayResult appPay(@RequestBody AliPayAppPayParam param) {
        return aliPayService.appPay(param);
    }

    @ApiOperation(value = "手机网站支付", response = RestMessageVo.class)
    @PostMapping("wapPay")
    public void wapPay(HttpServletResponse httpResponse, @RequestBody AliPayWapPayParam param) {
        aliPayService.wapPay(httpResponse, param);
    }

    @ApiOperation(value = "电脑网站支付", response = RestMessageVo.class)
    @PostMapping("pagePay")
    public void pagePay(HttpServletResponse httpResponse, @RequestBody AliPayPagePayParam param) {
        aliPayService.pagePay(httpResponse, param);
    }

    @ApiOperation(value = "交易查询", response = RestMessageVo.class)
    @GetMapping("tradeQuery")
    public AliPayTradeQueryResult tradeQuery(String outTradeNo, String tradeNo) {
        return aliPayService.tradeQuery(outTradeNo, tradeNo);
    }

    @ApiOperation(value = "查询对账单下载地址", response = RestMessageVo.class)
    @PostMapping("queryBillDownloadUrl")
    public String queryBillDownloadUrl(@RequestParam String billDate) {
        return aliPayService.queryBillDownloadUrl(billDate);
    }

    @ApiOperation(value = "退款", response = RestMessageVo.class)
    @PostMapping("tradeRefund")
    public AliPayTradeRefundResult tradeRefund(@RequestBody AliPayTradeRefundParam param) {
        return aliPayService.tradeRefund(param);
    }

    @ApiOperation(value = "退款查询", response = RestMessageVo.class)
    @PostMapping("refundQuery")
    public AliPayRefundQueryResult refundQuery(@RequestParam String outRequestNo, String outTradeNo, String tradeNo) {
        return aliPayService.refundQuery(outRequestNo, outTradeNo, tradeNo);
    }
}
