package com.xwbing.service.pay;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.xwbing.exception.PayException;
import com.xwbing.service.pay.enums.AliPayTradeStatusEnum;
import com.xwbing.service.pay.vo.AliPayTradeRefundParam;
import com.xwbing.service.pay.vo.AliPayTradeRefundResult;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年07月01日 下午1:16
 */
@Service
@Slf4j
@PropertySource("classpath:pay.properties")
public class AliPayBaseService {
    @Value("${aliPay.certificatePath}")
    private String certificatePath;
    @Value("${aliPay.userId}")
    public String aliPayUserId;
    @Value("${aliPay.serverUrl}")
    public String serverUrl;
    /**
     * 支付宝分配给开发者的应用ID
     */
    @Value("${aliPay.appId}")
    public String appId;
    /**
     * 应用私钥
     */
    @Value("${aliPay.appPrivateKey}")
    public String appPrivateKey;
    /**
     * 支付宝公钥
     */
    @Value("${aliPay.aliPayPublicKey}")
    public String aliPayPublicKey;
    /**
     * 证书客户端
     */
    private volatile AlipayClient aliPayCertClient;
    /**
     * 默认客户端
     */
    private volatile AlipayClient alipayClient;

    public AlipayClient getAliPayClient() {
        if (alipayClient == null) {
            synchronized (AliPayTransferService.class) {
                if (alipayClient == null) {
                    alipayClient = new DefaultAlipayClient(serverUrl, appId, appPrivateKey, "json", "UTF-8",
                            aliPayPublicKey, "RSA2");
                    log.info("initAliPayClient success");
                }
            }
        }
        return aliPayCertClient;
    }

    public AlipayClient getAliPayCertClient() {
        if (aliPayCertClient == null) {
            synchronized (AliPayTransferService.class) {
                if (aliPayCertClient == null) {
                    try {
                        CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
                        certAlipayRequest.setServerUrl(serverUrl);
                        certAlipayRequest.setAppId(appId);
                        certAlipayRequest.setPrivateKey(appPrivateKey);
                        certAlipayRequest.setFormat("json");
                        certAlipayRequest.setCharset("UTF-8");
                        certAlipayRequest.setSignType("RSA2");
                        certAlipayRequest.setCertPath(certificatePath + "/appCertPublicKey.crt");
                        certAlipayRequest.setAlipayPublicCertPath(certificatePath + "/alipayCertPublicKey_RSA2.crt");
                        certAlipayRequest.setRootCertPath(certificatePath + "/alipayRootCert.crt");
                        aliPayCertClient = new DefaultAlipayClient(certAlipayRequest);
                        log.info("initAliPayCertClient success");
                    } catch (Exception e) {
                        log.error("initAliPayCertClient error", e);
                    }
                }
            }
        }
        return aliPayCertClient;
    }

    /**
     * 查询对账单下载地址
     * 日账单格式为yyyy-MM-dd 当天无法查询
     * 月账单格式为yyyy-MM 当月无法查询
     * 10点后才会生成昨天账单
     *
     * @param billDate
     *
     * @return
     */
    public String queryBillDownloadUrl(String billDate) {
        try {
            log.info("queryBillDownloadUrl billDate:{}", billDate);
            AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
            Map<String, Object> bizContent = new HashMap<>(2);
            bizContent.put("bill_type", "signcustomer");
            bizContent.put("bill_date", billDate);
            request.setBizContent(JSONObject.toJSONString(bizContent));
            AlipayDataDataserviceBillDownloadurlQueryResponse response = getAliPayClient().execute(request);
            log.info("queryBillDownloadUrl response:{}", JSONObject.toJSONString(response));
            if (response.isSuccess()) {
                return response.getBillDownloadUrl();
            } else {
                throw new PayException("查询对账单下载地址异常");
            }
        } catch (Exception e) {
            log.error("queryBillDownloadUrl error", e);
            throw new PayException("查询对账单下载地址异常");
        }
    }

    /**
     * 统一收单线下交易查询
     *
     * @param outTradeNo 商户订单号 商户订单号和支付宝交易号2选1
     * @param tradeNo 支付宝交易号(推荐) 商户订单号和支付宝交易号2选1
     *
     * @return
     */
    public AliPayTradeStatusEnum tradeQuery(String outTradeNo, String tradeNo) {
        try {
            log.info("tradeQuery outTradeNo:{} tradeNo:{}", outTradeNo, tradeNo);
            if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(tradeNo)) {
                throw new PayException("商户订单号和支付宝交易号不能同时为空");
            }
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            JSONObject jsonObject = new JSONObject();
            if (StringUtils.isNotEmpty(outTradeNo)) {
                jsonObject.put("out_trade_no", outTradeNo);
            }
            if (StringUtils.isNotEmpty(tradeNo)) {
                jsonObject.put("trade_no", tradeNo);
            }
            request.setBizContent(jsonObject.toString());
            AlipayTradeQueryResponse response = getAliPayClient().execute(request);
            log.info("tradeQuery outTradeNo:{} tradeNo:{} response:{}", outTradeNo, tradeNo,
                    JSONObject.toJSONString(response));
            if (response.isSuccess()) {
                return AliPayTradeStatusEnum.parse(response.getTradeStatus());
            } else {
                throw new PayException("查询对账单下载地址异常");
            }
        } catch (Exception e) {
            log.error("tradeQuery outTradeNo:{} tradeNo:{} error", outTradeNo, tradeNo, e);
            throw new PayException("统一收单线下交易查询异常");
        }
    }

    /**
     * 统一收单交易退款
     *
     * @param request
     *
     * @return
     */
    public AliPayTradeRefundResult tradeRefund(AliPayTradeRefundParam request) {
        String outTradeNo = request.getOutTradeNo();
        try {
            log.info("tradeRefund outTradeNo:{} request:{}", outTradeNo, JSONObject.toJSONString(request));
            if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(request.getTradeNo())) {
                throw new PayException("商户订单号和支付宝交易号不能同时为空");
            }
            if (request.getRefundAmount() == 0) {
                throw new PayException("退款金额不能为空");
            }
            AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
            refundRequest.setBizContent(JSONObject.toJSONString(request));
            AlipayTradeRefundResponse response = getAliPayClient().execute(refundRequest);
            log.info("tradeRefund outTradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(request),
                    JSONObject.toJSONString(response));
            if (response.isSuccess() && "Y".equals(response.getFundChange())) {
                return AliPayTradeRefundResult.builder().success(true).refundTime(response.getGmtRefundPay())
                        .message(response.getMsg()).code(response.getCode()).build();
            } else {
                return AliPayTradeRefundResult.builder().success(false).code(response.getSubCode())
                        .message(response.getSubMsg()).build();
            }
        } catch (Exception e) {
            log.error("tradeRefund outTradeNo:{} error", outTradeNo, JSONObject.toJSONString(request), e);
            return AliPayTradeRefundResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
        }
    }
}