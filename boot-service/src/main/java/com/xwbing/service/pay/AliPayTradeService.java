package com.xwbing.service.pay;

import java.math.BigDecimal;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayDataDataserviceBillDownloadurlQueryModel;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeCreateModel;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.xwbing.exception.PayException;
import com.xwbing.service.pay.vo.AliPayPagePayParam;
import com.xwbing.service.pay.vo.AliPayRefundQueryResult;
import com.xwbing.service.pay.vo.AliPayTradeCloseResult;
import com.xwbing.service.pay.vo.AliPayTradeCreateParam;
import com.xwbing.service.pay.vo.AliPayTradeCreateResult;
import com.xwbing.service.pay.vo.AliPayTradePayParam;
import com.xwbing.service.pay.vo.AliPayTradePayResult;
import com.xwbing.service.pay.vo.AliPayTradeQueryResult;
import com.xwbing.service.pay.vo.AliPayTradeRefundParam;
import com.xwbing.service.pay.vo.AliPayTradeRefundResult;
import com.xwbing.service.pay.vo.AliPayWapPayParam;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xwbing
 */
@Slf4j
@Service
@PropertySource("classpath:pay.properties")
public class AliPayTradeService {
    @Value("${aliPay.payGateWay:}")
    private String payGateWay;
    @Resource
    private AlipayClient aliPayCertClient;

    /**
     * 统一收单交易创建
     * 商户通过该接口进行交易的创建下单
     *
     * @param param
     *
     * @return
     */
    public AliPayTradeCreateResult tradeCreate(AliPayTradeCreateParam param) {
        String outTradeNo = param.getOutTradeNo();
        try {
            String checkResult = AliPayTradeCreateParam.checkParam(param);
            if (StringUtils.isNotEmpty(checkResult)) {
                throw new PayException(checkResult);
            }
            AlipayTradeCreateModel model = new AlipayTradeCreateModel();
            model.setOutTradeNo(param.getOutTradeNo());
            model.setTotalAmount(param.getTotalAmount().toString());
            model.setSubject(param.getSubject());
            model.setBuyerId(param.getBuyerId());
            model.setTimeoutExpress("10m");
            AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
            //异步回调通知地址
            if (StringUtils.isNotEmpty(payGateWay)) {
                request.setNotifyUrl(payGateWay + "/payNotice/aliPay/tradeCreate");
            }
            request.setBizModel(model);
            log.info("tradeCreate outTradeNo:{} request:{}", outTradeNo, JSONObject.toJSONString(request));
            AlipayTradeCreateResponse response = aliPayCertClient.certificateExecute(request);
            log.info("tradeCreate outTradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            return response.isSuccess() ?
                    AliPayTradeCreateResult.ofSuccess(response) :
                    AliPayTradeCreateResult.ofFail(response);
        } catch (Exception e) {
            log.error("tradeCreate outTradeNo:{} error", outTradeNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return AliPayTradeCreateResult.ofError();
        }
    }

    /**
     * 统一收单交易关闭
     * 用于交易创建后，用户在一定时间内未进行支付，可调用该接口直接将未付款的交易进行关闭
     *
     * @param outTradeNo
     * @param tradeNo
     *
     * @return
     */
    public AliPayTradeCloseResult tradeClose(String outTradeNo, String tradeNo) {
        try {
            log.info("tradeClose outTradeNo:{} tradeNo:{}", outTradeNo, tradeNo);
            if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(tradeNo)) {
                throw new PayException("商户订单号和支付宝交易号不能同时为空");
            }
            AlipayTradeCloseModel model = new AlipayTradeCloseModel();
            if (StringUtils.isNotEmpty(outTradeNo)) {
                model.setOutTradeNo(outTradeNo);
            }
            if (StringUtils.isNotEmpty(tradeNo)) {
                model.setTradeNo(tradeNo);
            }
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            request.setBizModel(model);
            AlipayTradeCloseResponse response = aliPayCertClient.certificateExecute(request);
            log.info("tradeClose outTradeNo:{} tradeNo:{} response:{}", outTradeNo, tradeNo,
                    JSONObject.toJSONString(response));
            return response.isSuccess() ?
                    AliPayTradeCloseResult.ofSuccess(response) :
                    AliPayTradeCloseResult.ofFail(response);
        } catch (Exception e) {
            log.error("tradeClose outTradeNo:{} tradeNo:{} error", outTradeNo, tradeNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return AliPayTradeCloseResult.ofError();
        }
    }

    /**
     * 统一收单交易支付
     * 条码支付|声波支付
     *
     * @param param
     *
     * @return
     */
    public AliPayTradePayResult tradePay(AliPayTradePayParam param) {
        String outTradeNo = param.getOutTradeNo();
        try {
            String checkResult = AliPayTradePayParam.checkParam(param);
            if (StringUtils.isNotEmpty(checkResult)) {
                throw new PayException(checkResult);
            }
            AlipayTradePayModel model = new AlipayTradePayModel();
            model.setOutTradeNo(param.getOutTradeNo());
            model.setScene(param.getScene());
            model.setAuthCode(param.getAuthCode());
            model.setSubject(param.getSubject());
            model.setTotalAmount(param.getTotalAmount().toString());
            model.setTimeoutExpress("10m");
            AlipayTradePayRequest request = new AlipayTradePayRequest();
            if (StringUtils.isNotEmpty(payGateWay)) {
                request.setNotifyUrl(payGateWay + "/payNotice/aliPay/tradePay");
            }
            request.setBizModel(model);
            log.info("tradePay outTradeNo:{} request:{}", outTradeNo, JSONObject.toJSONString(request));
            AlipayTradePayResponse response = aliPayCertClient.certificateExecute(request);
            log.info("tradePay outTradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            return response.isSuccess() ?
                    AliPayTradePayResult.ofSuccess(response) :
                    AliPayTradePayResult.ofFail(response);
        } catch (Exception e) {
            log.error("aliPayTradePay outTradeNo:{} error", outTradeNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return AliPayTradePayResult.ofError();
        }
    }

    /**
     * 手机网站支付
     *
     * @param httpResponse
     * @param param
     */
    public void wapPay(HttpServletResponse httpResponse, AliPayWapPayParam param) {
        String outTradeNo = param.getOutTradeNo();
        try {
            AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
            model.setSubject(param.getSubject());
            model.setOutTradeNo(param.getOutTradeNo());
            model.setTotalAmount(param.getTotalAmount().toString());
            model.setQuitUrl(param.getQuitUrl());
            model.setProductCode("QUICK_WAP_WAY");
            model.setTimeoutExpress("10m");
            AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
            if (StringUtils.isNotEmpty(payGateWay)) {
                request.setNotifyUrl(payGateWay + "/payNotice/aliPay/tradePay");
            }
            if (StringUtils.isNotEmpty(param.getReturnUrl())) {
                request.setReturnUrl(param.getReturnUrl());
            }
            request.setBizModel(model);
            log.info("wapPay outTradeNo:{} request:{}", outTradeNo, JSONObject.toJSONString(request));
            AlipayTradeWapPayResponse response = aliPayCertClient.pageExecute(request);
            log.info("wapPay outTradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            String form = response.getBody();
            httpResponse.setContentType("text/html;charset=utf-8");
            //直接将完整的表单html输出到页面
            httpResponse.getWriter().write(form);
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        } catch (Exception e) {
            log.error("wapPay outTradeNo:{} error", outTradeNo, e);
            throw new PayException("手机网站支付异常");
        }
    }

    /**
     * 电脑网站支付
     *
     * @param httpResponse
     * @param param
     */
    public void pagePay(HttpServletResponse httpResponse, AliPayPagePayParam param) {
        String outTradeNo = param.getOutTradeNo();
        try {
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(param.getOutTradeNo());
            model.setTotalAmount(param.getTotalAmount().toString());
            model.setSubject(param.getSubject());
            model.setProductCode("FAST_INSTANT_TRADE_PAY");
            model.setTimeoutExpress("10m");
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            if (StringUtils.isNotEmpty(payGateWay)) {
                request.setNotifyUrl(payGateWay + "/payNotice/aliPay/tradePay");
            }
            if (StringUtils.isNotEmpty(param.getReturnUrl())) {
                request.setReturnUrl(param.getReturnUrl());
            }
            request.setBizModel(model);
            log.info("pagePay outTradeNo:{} request:{}", outTradeNo, JSONObject.toJSONString(request));
            AlipayTradePagePayResponse response = aliPayCertClient.pageExecute(request);
            log.info("pagePay outTradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            String form = response.getBody();
            httpResponse.setContentType("text/html;charset=utf-8");
            //直接将完整的表单html输出到页面
            httpResponse.getWriter().write(form);
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        } catch (Exception e) {
            log.error("pagePay outTradeNo:{} error", outTradeNo, e);
            throw new PayException("电脑网站支付异常");
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
    public AliPayTradeQueryResult tradeQuery(String outTradeNo, String tradeNo) {
        try {
            log.info("tradeQuery outTradeNo:{} tradeNo:{}", outTradeNo, tradeNo);
            if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(tradeNo)) {
                throw new PayException("商户订单号和支付宝交易号不能同时为空");
            }
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            if (StringUtils.isNotEmpty(outTradeNo)) {
                model.setOutTradeNo(outTradeNo);
            }
            if (StringUtils.isNotEmpty(tradeNo)) {
                model.setTradeNo(tradeNo);
            }
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            request.setBizModel(model);
            AlipayTradeQueryResponse response = aliPayCertClient.certificateExecute(request);
            log.info("tradeQuery outTradeNo:{} tradeNo:{} response:{}", outTradeNo, tradeNo,
                    JSONObject.toJSONString(response));
            return response.isSuccess() ?
                    AliPayTradeQueryResult.ofSuccess(response) :
                    AliPayTradeQueryResult.ofFail(response);
        } catch (Exception e) {
            log.error("tradeQuery outTradeNo:{} tradeNo:{} error", outTradeNo, tradeNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return AliPayTradeQueryResult.ofError();
        }
    }

    /**
     * 统一收单交易退款
     *
     * @param param
     *
     * @return
     */
    public AliPayTradeRefundResult tradeRefund(AliPayTradeRefundParam param) {
        String outTradeNo = param.getOutTradeNo();
        try {
            log.info("tradeRefund outTradeNo:{} param:{}", outTradeNo, JSONObject.toJSONString(param));
            if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(param.getTradeNo())) {
                throw new PayException("商户订单号和支付宝交易号不能同时为空");
            }
            BigDecimal refundAmount = Optional.ofNullable(param.getRefundAmount()).orElse(BigDecimal.ZERO);
            if (refundAmount.compareTo(BigDecimal.ZERO) < 1) {
                throw new PayException("退款金额不能为小于0元");
            }
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(param.getOutTradeNo());
            model.setTradeNo(param.getTradeNo());
            model.setOutRequestNo(param.getOutRequestNo());
            model.setRefundAmount(param.getRefundAmount().toString());
            model.setRefundReason(param.getRefundReason());
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            request.setBizModel(model);
            AlipayTradeRefundResponse response = aliPayCertClient.certificateExecute(request);
            log.info("tradeRefund outTradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            return response.isSuccess() && "Y".equals(response.getFundChange()) ?
                    AliPayTradeRefundResult.ofSuccess(response) :
                    AliPayTradeRefundResult.ofFail(response);
        } catch (Exception e) {
            log.error("tradeRefund outTradeNo:{} error", outTradeNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return AliPayTradeRefundResult.ofError();
        }
    }

    /**
     * 统一收单交易退款查询
     * 商户订单号和支付宝交易号2选1
     * 如果有查询数据，且refund_status为空或为REFUND_SUCCESS，则代表退款成功
     *
     * @param outTradeNo 商户订单号
     * @param tradeNo 支付宝交易号(推荐)
     * @param outRequestNo 退款请求号
     *
     * @return
     */
    public AliPayRefundQueryResult refundQuery(String outRequestNo, String outTradeNo, String tradeNo) {
        try {
            if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(tradeNo)) {
                throw new PayException("商户订单号和支付宝交易号不能同时为空!");
            }
            AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
            model.setOutRequestNo(outRequestNo);
            if (StringUtils.isNotEmpty(outTradeNo)) {
                model.setOutTradeNo(outTradeNo);
            }
            if (StringUtils.isNotEmpty(tradeNo)) {
                model.setTradeNo(tradeNo);
            }
            AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
            request.setBizModel(model);
            log.info("refundQuery outRequestNo:{} request:{}", outRequestNo, JSONObject.toJSONString(request));
            AlipayTradeFastpayRefundQueryResponse response = aliPayCertClient.certificateExecute(request);
            log.info("refundQuery outRequestNo:{} response:{}", outRequestNo, JSONObject.toJSONString(response));
            return response.isSuccess() ?
                    AliPayRefundQueryResult.ofSuccess(response) :
                    AliPayRefundQueryResult.ofFail(response);
        } catch (Exception e) {
            log.error("refundQuery outRequestNo:{} error", outRequestNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return AliPayRefundQueryResult.ofError();
        }
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
            AlipayDataDataserviceBillDownloadurlQueryModel model = new AlipayDataDataserviceBillDownloadurlQueryModel();
            model.setBillType("signcustomer");
            model.setBillDate(billDate);
            request.setBizModel(model);
            AlipayDataDataserviceBillDownloadurlQueryResponse response = aliPayCertClient.certificateExecute(request);
            log.info("queryBillDownloadUrl response:{}", JSONObject.toJSONString(response));
            if (response.isSuccess()) {
                return response.getBillDownloadUrl();
            }
        } catch (Exception e) {
            log.error("queryBillDownloadUrl error", e);
        }
        return null;
    }
}
