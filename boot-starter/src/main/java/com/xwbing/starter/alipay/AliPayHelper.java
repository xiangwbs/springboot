package com.xwbing.starter.alipay;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayDataDataserviceBillDownloadurlQueryModel;
import com.alipay.api.domain.AlipayFundAccountQueryModel;
import com.alipay.api.domain.AlipayFundTransCommonQueryModel;
import com.alipay.api.domain.AlipayFundTransUniTransferModel;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeCancelModel;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeCreateModel;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradePayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.domain.Participant;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayFundAccountQueryRequest;
import com.alipay.api.request.AlipayFundTransCommonQueryRequest;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayFundAccountQueryResponse;
import com.alipay.api.response.AlipayFundTransCommonQueryResponse;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.xwbing.starter.alipay.enums.AliPayRefundStatusEnum;
import com.xwbing.starter.alipay.enums.TransferStatusEnum;
import com.xwbing.starter.alipay.vo.request.AliPayAppPayParam;
import com.xwbing.starter.alipay.vo.request.AliPayExtendParam;
import com.xwbing.starter.alipay.vo.request.AliPayPagePayParam;
import com.xwbing.starter.alipay.vo.request.AliPayTradeCreateParam;
import com.xwbing.starter.alipay.vo.request.AliPayTradePayParam;
import com.xwbing.starter.alipay.vo.request.AliPayTradePreCreateParam;
import com.xwbing.starter.alipay.vo.request.AliPayTradeRefundParam;
import com.xwbing.starter.alipay.vo.request.AliPayTransferParam;
import com.xwbing.starter.alipay.vo.request.AliPayWapPayParam;
import com.xwbing.starter.alipay.vo.response.AliPayRefundQueryResult;
import com.xwbing.starter.alipay.vo.response.AliPayTradeCloseResult;
import com.xwbing.starter.alipay.vo.response.AliPayTradeCreateResult;
import com.xwbing.starter.alipay.vo.response.AliPayTradePayResult;
import com.xwbing.starter.alipay.vo.response.AliPayTradePreCreateResult;
import com.xwbing.starter.alipay.vo.response.AliPayTradeQueryResult;
import com.xwbing.starter.alipay.vo.response.AliPayTradeRefundResult;
import com.xwbing.starter.alipay.vo.response.AliPayTransferQueryResult;
import com.xwbing.starter.alipay.vo.response.AliPayTransferResult;
import com.xwbing.starter.exception.PayException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xwbing
 */
@Slf4j
public class AliPayHelper {
    private final AlipayClient aliPayCertClient;
    private final AliPayProperties properties;

    public AliPayHelper(AlipayClient aliPayCertClient, AliPayProperties properties) {
        this.aliPayCertClient = aliPayCertClient;
        this.properties = properties;
    }

    public AliPayProperties getProperties() {
        return properties;
    }

    /**
     * 当面付(用户扫码支付)
     * 统一收单线下交易预创建
     * 扫码支付
     * 支持花呗
     * TRADE_SUCCESS 触发异步通知
     *
     * @param param
     *
     * @return
     */
    public AliPayTradePreCreateResult tradePreCreate(AliPayTradePreCreateParam param) {
        String outTradeNo = param.getTradeNo();
        try {
            AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
            model.setOutTradeNo(outTradeNo);
            model.setTotalAmount(param.getTotalAmount().toString());
            model.setSubject(param.getSubject());
            model.setTimeoutExpress("10m");
            AliPayExtendParam extendParam = param.getExtendParam();
            if (extendParam != null) {
                model.setExtendParams(extendParam.convert());
            }
            AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
            if (StringUtils.isNotEmpty(param.getNotifyUrl())) {
                request.setNotifyUrl(param.getNotifyUrl());
            }
            request.setBizModel(model);
            log.info("tradePreCreate tradeNo:{} request:{}", outTradeNo, JSONObject.toJSONString(request));
            AlipayTradePrecreateResponse response = aliPayCertClient.certificateExecute(request);
            log.info("tradePreCreate tradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            return response.isSuccess() ?
                    AliPayTradePreCreateResult.ofSuccess(response) :
                    AliPayTradePreCreateResult.ofFail(response);
        } catch (Exception e) {
            log.error("tradePreCreate tradeNo:{} error", outTradeNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return AliPayTradePreCreateResult.ofError();
        }
    }

    /**
     * 当面付(用户付款码支付)
     * 统一收单交易支付
     * 条码支付|声波支付
     * 支持花呗
     * TRADE_SUCCESS 触发异步通知
     *
     * @param param
     *
     * @return
     */
    public AliPayTradePayResult tradePay(AliPayTradePayParam param) {
        String outTradeNo = param.getTradeNo();
        try {
            String checkResult = AliPayTradePayParam.checkParam(param);
            if (StringUtils.isNotEmpty(checkResult)) {
                throw new PayException(checkResult);
            }
            AlipayTradePayModel model = new AlipayTradePayModel();
            model.setOutTradeNo(outTradeNo);
            model.setTotalAmount(param.getTotalAmount().toString());
            model.setSubject(param.getSubject());
            model.setScene(param.getScene());
            model.setAuthCode(param.getAuthCode());
            model.setTimeoutExpress("10m");
            AliPayExtendParam extendParam = param.getExtendParam();
            if (extendParam != null) {
                model.setExtendParams(extendParam.convert());
            }
            AlipayTradePayRequest request = new AlipayTradePayRequest();
            if (StringUtils.isNotEmpty(param.getNotifyUrl())) {
                request.setNotifyUrl(param.getNotifyUrl());
            }
            request.setBizModel(model);
            log.info("tradePay tradeNo:{} request:{}", outTradeNo, JSONObject.toJSONString(request));
            AlipayTradePayResponse response = aliPayCertClient.certificateExecute(request);
            log.info("tradePay tradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            return response.isSuccess() ?
                    AliPayTradePayResult.ofSuccess(response) :
                    AliPayTradePayResult.ofFail(response);
        } catch (Exception e) {
            log.error("tradePay tradeNo:{} error", outTradeNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return AliPayTradePayResult.ofError();
        }
    }

    /**
     * 当面付(小程序支付)
     * 统一收单交易创建
     * 支持花呗
     * TRADE_SUCCESS 触发异步通知
     *
     * @param param
     *
     * @return
     */
    public AliPayTradeCreateResult tradeCreate(AliPayTradeCreateParam param) {
        String outTradeNo = param.getTradeNo();
        try {
            String checkResult = AliPayTradeCreateParam.checkParam(param);
            if (StringUtils.isNotEmpty(checkResult)) {
                throw new PayException(checkResult);
            }
            AlipayTradeCreateModel model = new AlipayTradeCreateModel();
            model.setOutTradeNo(outTradeNo);
            model.setTotalAmount(param.getTotalAmount().toString());
            model.setSubject(param.getSubject());
            model.setBuyerId(param.getBuyerId());
            model.setTimeoutExpress("10m");
            AliPayExtendParam extendParam = param.getExtendParam();
            if (extendParam != null) {
                model.setExtendParams(extendParam.convert());
            }
            AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
            if (StringUtils.isNotEmpty(param.getNotifyUrl())) {
                request.setNotifyUrl(param.getNotifyUrl());
            }
            request.setBizModel(model);
            log.info("tradeCreate tradeNo:{} request:{}", outTradeNo, JSONObject.toJSONString(request));
            AlipayTradeCreateResponse response = aliPayCertClient.certificateExecute(request);
            log.info("tradeCreate tradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            return response.isSuccess() ?
                    AliPayTradeCreateResult.ofSuccess(response) :
                    AliPayTradeCreateResult.ofFail(response);
        } catch (Exception e) {
            log.error("tradeCreate tradeNo:{} error", outTradeNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return AliPayTradeCreateResult.ofError();
        }
    }

    /**
     * 电脑网站支付
     * 支持花呗
     * TRADE_SUCCESS 触发异步通知
     *
     * @param param
     */
    public String pagePay(AliPayPagePayParam param) {
        String outTradeNo = param.getTradeNo();
        try {
            AlipayTradePagePayModel model = new AlipayTradePagePayModel();
            model.setOutTradeNo(outTradeNo);
            model.setTotalAmount(param.getTotalAmount().toString());
            model.setSubject(param.getSubject());
            model.setProductCode("FAST_INSTANT_TRADE_PAY");
            model.setTimeoutExpress("10m");
            AliPayExtendParam extendParam = param.getExtendParam();
            if (extendParam != null) {
                model.setExtendParams(extendParam.convert());
            }
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            if (StringUtils.isNotEmpty(param.getNotifyUrl())) {
                request.setNotifyUrl(param.getNotifyUrl());
            }
            if (StringUtils.isNotEmpty(param.getReturnUrl())) {
                request.setReturnUrl(param.getReturnUrl());
            }
            request.setBizModel(model);
            log.info("pagePay tradeNo:{} request:{}", outTradeNo, JSONObject.toJSONString(request));
            AlipayTradePagePayResponse response = aliPayCertClient.pageExecute(request);
            log.info("pagePay tradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            return response.getBody();
        } catch (Exception e) {
            log.error("pagePay tradeNo:{} error", outTradeNo, e);
            throw new PayException("电脑网站支付异常");
        }
    }

    /**
     * 手机网站支付
     * 支持花呗
     * TRADE_SUCCESS TRADE_CLOSED TRADE_FINISHED 触发异步通知
     *
     * @param param
     */
    public String wapPay(AliPayWapPayParam param) {
        String outTradeNo = param.getTradeNo();
        try {
            AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
            model.setSubject(param.getSubject());
            model.setOutTradeNo(outTradeNo);
            model.setTotalAmount(param.getTotalAmount().toString());
            model.setQuitUrl(param.getQuitUrl());
            model.setProductCode("QUICK_WAP_WAY");
            model.setTimeoutExpress("10m");
            AliPayExtendParam extendParam = param.getExtendParam();
            if (extendParam != null) {
                model.setExtendParams(extendParam.convert());
            }
            AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
            if (StringUtils.isNotEmpty(param.getNotifyUrl())) {
                request.setNotifyUrl(param.getNotifyUrl());
            }
            if (StringUtils.isNotEmpty(param.getReturnUrl())) {
                request.setReturnUrl(param.getReturnUrl());
            }
            request.setBizModel(model);
            log.info("wapPay tradeNo:{} request:{}", outTradeNo, JSONObject.toJSONString(request));
            AlipayTradeWapPayResponse response = aliPayCertClient.pageExecute(request);
            log.info("wapPay tradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            return response.getBody();
        } catch (Exception e) {
            log.error("wapPay tradeNo:{} error", outTradeNo, e);
            throw new PayException("手机网站支付异常");
        }
    }

    /**
     * app支付
     * 支持花呗
     * TRADE_SUCCESS TRADE_CLOSED TRADE_FINISHED 触发异步通知
     *
     * @param param
     *
     * @return
     */
    public String appPay(AliPayAppPayParam param) {
        String outTradeNo = param.getTradeNo();
        try {
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setOutTradeNo(outTradeNo);
            model.setSubject(param.getSubject());
            model.setTotalAmount(param.getTotalAmount().toString());
            model.setProductCode("QUICK_MSECURITY_PAY");
            model.setTimeoutExpress("10m");
            AliPayExtendParam extendParam = param.getExtendParam();
            if (extendParam != null) {
                model.setExtendParams(extendParam.convert());
            }
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            if (StringUtils.isNotEmpty(param.getNotifyUrl())) {
                request.setNotifyUrl(param.getNotifyUrl());
            }
            request.setBizModel(model);
            log.info("appPay tradeNo:{} request:{}", outTradeNo, JSONObject.toJSONString(request));
            AlipayTradeAppPayResponse response = aliPayCertClient.sdkExecute(request);
            log.info("appPay tradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            return response.getBody();
        } catch (Exception e) {
            log.error("appPay tradeNo:{} error", outTradeNo, e);
            throw new PayException("app支付异常");
        }
    }

    /**
     * 统一收单交易退款
     * 异步通知是依据支付接口的触发条件来触发的，异步通知也是发送到支付接口传入的异步地址上
     *
     * 部分退款交易状态是处于TRADE_SUCCESS（交易成功）
     * 交易成功后全额退款，交易状态会转为TRADE_CLOSED（交易关闭）
     *
     * @param param
     *
     * @return
     */
    public AliPayTradeRefundResult tradeRefund(AliPayTradeRefundParam param) {
        String tradeNo = param.getTradeNo();
        try {
            log.info("tradeRefund tradeNo:{} param:{}", tradeNo, JSONObject.toJSONString(param));
            if (StringUtils.isEmpty(tradeNo) && StringUtils.isEmpty(param.getOutTradeNo())) {
                throw new PayException("商户订单号和支付宝交易号不能同时为空");
            }
            BigDecimal refundAmount = Optional.ofNullable(param.getRefundAmount()).orElse(BigDecimal.ZERO);
            if (refundAmount.compareTo(BigDecimal.ZERO) < 1) {
                throw new PayException("退款金额不能为小于0元");
            }
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            model.setOutTradeNo(tradeNo);
            model.setTradeNo(param.getOutTradeNo());
            model.setOutRequestNo(param.getRequestNo());
            model.setRefundAmount(param.getRefundAmount().toString());
            model.setRefundReason(param.getRefundReason());
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            request.setBizModel(model);
            AlipayTradeRefundResponse response = aliPayCertClient.certificateExecute(request);
            log.info("tradeRefund tradeNo:{} response:{}", tradeNo, JSONObject.toJSONString(response));
            return response.isSuccess() && "Y".equals(response.getFundChange()) ?
                    AliPayTradeRefundResult.ofSuccess(response) :
                    AliPayTradeRefundResult.ofFail(response);
        } catch (Exception e) {
            log.error("tradeRefund tradeNo:{} error", tradeNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return AliPayTradeRefundResult.ofError();
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
    public AliPayTradeCloseResult tradeClose(String tradeNo, String outTradeNo) {
        try {
            log.info("tradeClose tradeNo:{} outTradeNo:{} ", tradeNo, outTradeNo);
            if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(tradeNo)) {
                throw new PayException("商户订单号和支付宝交易号不能同时为空");
            }
            AlipayTradeCloseModel model = new AlipayTradeCloseModel();
            if (StringUtils.isNotEmpty(tradeNo)) {
                model.setOutTradeNo(tradeNo);
            }
            if (StringUtils.isNotEmpty(outTradeNo)) {
                model.setTradeNo(outTradeNo);
            }
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            request.setBizModel(model);
            AlipayTradeCloseResponse response = aliPayCertClient.certificateExecute(request);
            log.info("tradeClose tradeNo:{} outTradeNo:{} response:{}", tradeNo, outTradeNo,
                    JSONObject.toJSONString(response));
            return response.isSuccess() ?
                    AliPayTradeCloseResult.ofSuccess(response) :
                    AliPayTradeCloseResult.ofFail(response);
        } catch (Exception e) {
            log.error("tradeClose tradeNo:{} outTradeNo:{} error", tradeNo, outTradeNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return AliPayTradeCloseResult.ofError();
        }
    }

    /**
     * 统一收单交易撤销
     * 支付交易返回失败或支付系统超时，调用该接口撤销交易
     *
     * @param tradeNo
     * @param outTradeNo
     *
     * @return
     */
    public boolean tradeCancel(String tradeNo, String outTradeNo) {
        try {
            log.info("tradeCancel tradeNo:{} outTradeNo:{} ", tradeNo, outTradeNo);
            if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(tradeNo)) {
                throw new PayException("商户订单号和支付宝交易号不能同时为空");
            }
            AlipayTradeCancelModel model = new AlipayTradeCancelModel();
            if (StringUtils.isNotEmpty(tradeNo)) {
                model.setOutTradeNo(tradeNo);
            }
            if (StringUtils.isNotEmpty(outTradeNo)) {
                model.setTradeNo(outTradeNo);
            }
            AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();
            request.setBizModel(model);
            AlipayTradeCancelResponse response = aliPayCertClient.certificateExecute(request);
            log.info("tradeClose tradeNo:{} outTradeNo:{} response:{}", tradeNo, outTradeNo,
                    JSONObject.toJSONString(response));
            return response.isSuccess() && StringUtils.isNotEmpty(response.getTradeNo());
        } catch (Exception e) {
            log.error("tradeClose tradeNo:{} outTradeNo:{} error", tradeNo, outTradeNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return false;
        }
    }

    /**
     * 统一收单线下交易查询
     *
     * @param tradeNo 商户订单号 商户订单号和支付宝交易号2选1
     * @param outTradeNo 支付宝交易号(推荐) 商户订单号和支付宝交易号2选1
     *
     * @return
     */
    public AliPayTradeQueryResult tradeQuery(String tradeNo, String outTradeNo) {
        try {
            log.info("tradeQuery tradeNo:{} outTradeNo:{}", tradeNo, outTradeNo);
            if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(tradeNo)) {
                throw new PayException("商户订单号和支付宝交易号不能同时为空");
            }
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            if (StringUtils.isNotEmpty(tradeNo)) {
                model.setOutTradeNo(tradeNo);
            }
            if (StringUtils.isNotEmpty(outTradeNo)) {
                model.setTradeNo(outTradeNo);
            }
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            request.setBizModel(model);
            AlipayTradeQueryResponse response = aliPayCertClient.certificateExecute(request);
            log.info("tradeQuery tradeNo:{} outTradeNo:{} response:{}", tradeNo, outTradeNo,
                    JSONObject.toJSONString(response));
            return response.isSuccess() ?
                    AliPayTradeQueryResult.ofSuccess(response) :
                    AliPayTradeQueryResult.ofFail(response);
        } catch (Exception e) {
            log.error("tradeQuery tradeNo:{} outTradeNo:{} error", tradeNo, outTradeNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return AliPayTradeQueryResult.ofError();
        }
    }

    /**
     * 统一收单交易退款查询
     * 商户订单号和支付宝交易号2选1
     * 如果有查询数据，且refund_status为空或为REFUND_SUCCESS，则代表退款成功
     *
     * @param tradeNo 商户订单号
     * @param outTradeNo 支付宝交易号(推荐)
     * @param requestNo 退款请求号
     *
     * @return
     */
    public AliPayRefundQueryResult refundQuery(String requestNo, String tradeNo, String outTradeNo) {
        try {
            if (StringUtils.isEmpty(requestNo)) {
                throw new PayException("退款请求号不能为空!");
            }
            if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(tradeNo)) {
                throw new PayException("商户订单号和支付宝交易号不能同时为空!");
            }
            AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
            model.setOutRequestNo(requestNo);
            model.setQueryOptions(Collections.singletonList("gmt_refund_pay"));
            if (StringUtils.isNotEmpty(tradeNo)) {
                model.setOutTradeNo(tradeNo);
            }
            if (StringUtils.isNotEmpty(outTradeNo)) {
                model.setTradeNo(outTradeNo);
            }
            AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
            request.setBizModel(model);
            log.info("refundQuery requestNo:{} request:{}", requestNo, JSONObject.toJSONString(request));
            AlipayTradeFastpayRefundQueryResponse response = aliPayCertClient.certificateExecute(request);
            log.info("refundQuery requestNo:{} response:{}", requestNo, JSONObject.toJSONString(response));
            boolean success;
            if (response.isSuccess() && response.getOutRequestNo().equals(requestNo)) {
                if (StringUtils.isNotEmpty(outTradeNo) && !outTradeNo.equals(response.getOutTradeNo())) {
                    success = false;
                } else if (StringUtils.isNotEmpty(tradeNo) && !tradeNo.equals(response.getTradeNo())) {
                    success = false;
                } else if (StringUtils.isNotEmpty(response.getRefundStatus()) && !AliPayRefundStatusEnum.TRADE_SUCCESS
                        .getCode().equals(response.getRefundStatus())) {
                    success = false;
                } else {
                    success = true;
                }
            } else {
                success = false;
            }
            return success ? AliPayRefundQueryResult.ofSuccess(response) : AliPayRefundQueryResult.ofFail(response);
        } catch (Exception e) {
            log.error("refundQuery requestNo:{} error", requestNo, e);
            if (e instanceof PayException) {
                ExceptionUtils.rethrow(e);
            }
            return AliPayRefundQueryResult.ofError();
        }
    }

    /**
     * 查询支付宝账户余额
     */
    public BigDecimal accountQuery() {
        try {
            log.info("accountQuery start");
            AlipayFundAccountQueryRequest request = new AlipayFundAccountQueryRequest();
            AlipayFundAccountQueryModel model = new AlipayFundAccountQueryModel();
            model.setAlipayUserId(properties.getUserId());
            model.setAccountType("ACCTRANS_ACCOUNT");
            request.setBizModel(model);
            AlipayFundAccountQueryResponse response = aliPayCertClient.certificateExecute(request);
            log.info("accountQuery response:{}", JSONObject.toJSONString(response));
            if (response.isSuccess()) {
                return new BigDecimal(response.getAvailableAmount());
            }
        } catch (Exception e) {
            log.error("accountQuery error", e);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 向指定支付宝账户转账
     *
     * @return
     */
    public AliPayTransferResult transfer(AliPayTransferParam param) {
        String outBizNo = param.getOutBizNo();
        try {
            AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
            AlipayFundTransUniTransferModel model = new AlipayFundTransUniTransferModel();
            model.setOutBizNo(outBizNo);
            model.setTransAmount(param.getAmount().toString());
            model.setProductCode("TRANS_ACCOUNT_NO_PWD");
            model.setBizScene("DIRECT_TRANSFER");
            model.setOrderTitle(param.getTitle());
            //收款方信息
            Participant participant = new Participant();
            participant.setIdentityType("ALIPAY_LOGON_ID");
            participant.setIdentity(param.getPayAccount());
            participant.setName(param.getName());
            model.setPayeeInfo(participant);
            request.setBizModel(model);
            log.info("transfer outBizNo:{} request:{}", outBizNo, JSONObject.toJSONString(request));
            AlipayFundTransUniTransferResponse response = aliPayCertClient.certificateExecute(request);
            log.info("transfer outBizNo:{} response:{}", outBizNo, JSONObject.toJSONString(response));
            //转账失败接口会直接同步返回错误，只要判断status=SUCCESS即可，如果出现其他都是不成功的
            return response.isSuccess() && TransferStatusEnum.SUCCESS.getCode().equals(response.getStatus()) ?
                    AliPayTransferResult.ofSuccess(response) :
                    AliPayTransferResult.ofFail(response);
        } catch (Exception e) {
            log.error("transfer outBizNo:{} amount:{} error", outBizNo, param.getAmount(), e);
            return AliPayTransferResult.ofError();
        }
    }

    /**
     * 查询转账结果
     *
     * @param outBizNo 商户转账唯一订单号
     *
     * @return
     */
    public AliPayTransferQueryResult transferQuery(String outBizNo) {
        try {
            AlipayFundTransCommonQueryRequest request = new AlipayFundTransCommonQueryRequest();
            AlipayFundTransCommonQueryModel model = new AlipayFundTransCommonQueryModel();
            //销售产品码 单笔无密转账到支付宝账户
            model.setProductCode("TRANS_ACCOUNT_NO_PWD");
            //业务场景 B2C现金红包、单笔无密转账
            model.setBizScene("DIRECT_TRANSFER");
            model.setOutBizNo(outBizNo);
            request.setBizModel(model);
            log.info("transferQuery outBizNo:{}", outBizNo);
            AlipayFundTransCommonQueryResponse response = aliPayCertClient.certificateExecute(request);
            log.info("transferQuery outBizNo:{} response:{}", outBizNo, JSONObject.toJSONString(response));
            //据响应参数status（转账单据状态）、pay_date（支付时间）参数判断
            return response.isSuccess() && TransferStatusEnum.SUCCESS.getCode().equals(response.getStatus())
                    && StringUtils.isNotEmpty(response.getPayDate()) ?
                    AliPayTransferQueryResult.ofSuccess(response) :
                    AliPayTransferQueryResult.ofFail(response);
        } catch (Exception e) {
            log.error("transferQuery outBizNo:{} error", outBizNo, e);
            return AliPayTransferQueryResult.ofError();
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