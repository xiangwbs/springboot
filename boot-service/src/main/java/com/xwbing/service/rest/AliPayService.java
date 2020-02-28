package com.xwbing.service.rest;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.xwbing.domain.entity.pay.alipay.*;
import com.xwbing.exception.PayException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

/**
 * 说明: 支付宝支付接口实现
 * 创建时间: 2017/5/10 17:50
 * 作者:  xiangwb
 */
@Slf4j
@Service
@PropertySource("classpath:pay.properties")
public class AliPayService {
    @Value("${aliPay.serverUrl}")
    private String serverUrl;
    /**
     * 异步回调通知地址
     */
    @Value("${aliPay.notifyUrl:}")
    private String notifyUrl;
    /**
     * 支付宝分配给开发者的应用ID
     */
    @Value("${aliPay.appId}")
    private String appId;
    /**
     * 私钥
     */
    @Value("${aliPay.rsaPrivateKey}")
    private String privateKey;
    /**
     * 公钥
     */
    @Value("${aliPay.rsaPublicKey}")
    private String publicKey;
    private AlipayClient alipayClient;

    public AliPayService() {
        alipayClient = new DefaultAlipayClient(serverUrl, appId, privateKey, "json", "UTF-8", publicKey, "RSA2");
    }

    /**
     * 统一收单交易支付
     * 条码支付|声波支付
     *
     * @param param
     * @return
     */
    public AliPayTradePayResult tradePay(AliPayTradePayParam param) {
        AliPayTradePayResult result = new AliPayTradePayResult(false);
        String checkResult = checkTradePayParam(param);
        if (StringUtils.isNotEmpty(checkResult)) {
            result.setMessage(checkResult);
            return result;
        }
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        //异步回调通知地址
        if (StringUtils.isNotEmpty(notifyUrl)) {
            request.setNotifyUrl(notifyUrl + "/aliPayNotice/tradePay");
        }
        request.setBizContent(JSONObject.toJSONString(param));
        AlipayTradePayResponse response;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("aliPayTradePay exception:{}", ExceptionUtils.getStackTrace(e));
            throw new PayException("统一收单交易支付异常");
        }
        log.info("aliPayTradePay:{}", JSONObject.toJSONString(response));
        boolean success = response.isSuccess();
        if (!success) {
            result.setCode(response.getSubCode());
            result.setMessage(response.getSubMsg());
            return result;
        } else {
            result.setCode(response.getCode());
            result.setMessage(response.getMsg());
            result.setSuccess(true);
        }
        result.setTradeNo(response.getTradeNo());
        result.setOutTradeNo(response.getOutTradeNo());
        result.setBuyerLogonId(response.getBuyerLogonId());
        result.setTotalAmount(response.getTotalAmount());
        result.setReceiptAmount(response.getReceiptAmount());
        result.setGmtPayment(response.getGmtPayment());
        result.setFundBillList(response.getFundBillList());
        result.setBuyerUserId(response.getBuyerUserId());
        return result;
    }

    /**
     * 统一收单交易退款
     *
     * @param param
     * @return
     */
    public AliPayTradeRefundResult tradeRefund(AliPayTradeRefundParam param) {
        AliPayTradeRefundResult result = new AliPayTradeRefundResult(false);
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent(JSONObject.toJSONString(param));
        AlipayTradeRefundResponse response;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("aliPayTradeRefund exception:{}", ExceptionUtils.getStackTrace(e));
            throw new PayException("统一收单交易退款异常");
        }
        log.info("aliPayTradeRefund:{}", JSONObject.toJSONString(response));
        if (response.isSuccess()) {
            if ("Y".equals(response.getFundChange())) {
                result.setCode(response.getCode());
                result.setMessage(response.getMsg());
                result.setSuccess(true);
            } else {
                result.setCode(response.getCode());
                result.setMessage("交易已退款");
                return result;
            }
        } else {
            result.setCode(response.getSubCode());
            result.setMessage(response.getSubMsg());
            return result;
        }
        result.setTradeNo(response.getTradeNo());
        result.setOutTradeNo(response.getOutTradeNo());
        result.setBuyerLogonId(response.getBuyerLogonId());
        result.setFundChange(response.getFundChange());
        result.setRefundFee(response.getRefundFee());
        result.setGmtRefundPay(response.getGmtRefundPay());
        result.setBuyerUserId(response.getBuyerUserId());
        return result;
    }

    /**
     * 统一收单线下交易查询
     * 根据商户订单号 支付宝交易号查询 只需要一个即可
     * 如果isSuccess，根据tradeStatus，遍历AliPayTradeStatusEnum获取对应支付状态
     *
     * @param outTradeNo 商户订单号
     * @param tradeNo    支付宝交易号(推荐)
     * @return
     */
    public AliPayQueryResult tradeQuery(String outTradeNo, String tradeNo) {
        if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(tradeNo)) {
            throw new PayException("商户订单号和支付宝交易号不能同时为空!");
        }
        AliPayQueryResult result = new AliPayQueryResult(false);
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isNotEmpty(outTradeNo)) {
            jsonObject.put("out_trade_no", outTradeNo);
        }
        if (StringUtils.isNotEmpty(tradeNo)) {
            jsonObject.put("trade_no", tradeNo);
        }
        request.setBizContent(jsonObject.toString());
        AlipayTradeQueryResponse response;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("tradeQuery exception:{}", ExceptionUtils.getStackTrace(e));
            throw new PayException("统一收单线下交易查询异常");
        }
        log.info("tradeQuery:{}", JSONObject.toJSONString(response));
        result.setSuccess(response.isSuccess());
        //如果subCode存在,代表调用接口失败
        checkTradeQuerySubCode(result, response);
        //返回支付状态,业务调用时,遍历AliPayTradeStatusEnum与tradeStatus做比较
        result.setTradeStatus(response.getTradeStatus());
        return result;
    }

    /**
     * 统一收单交易退款查询
     * 商户订单号和支付宝交易号2选1
     * 退款查询,没有tradeStatus。isSuccess即为成功
     *
     * @param outTradeNo   商户订单号
     * @param tradeNo      支付宝交易号(推荐)
     * @param outRequestNo 退款请求号
     * @return
     */
    public AliPayRefundQueryResult refundQuery(String outTradeNo, String tradeNo, String outRequestNo) {
        if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(tradeNo)) {
            throw new PayException("商户订单号和支付宝交易号不能同时为空!");
        }
        AliPayRefundQueryResult result = new AliPayRefundQueryResult(false);
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_request_no", outRequestNo);
        if (StringUtils.isNotEmpty(outTradeNo)) {
            jsonObject.put("out_trade_no", outTradeNo);
        }
        if (StringUtils.isNotEmpty(tradeNo)) {
            jsonObject.put("trade_no", tradeNo);
        }
        request.setBizContent(jsonObject.toString());
        AlipayTradeFastpayRefundQueryResponse response;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("refundQuery exception:{}", ExceptionUtils.getStackTrace(e));
            throw new PayException("统一收单交易退款查询异常");
        }
        log.info("refundQuery:{}", JSONObject.toJSONString(response));
        result.setSuccess(response.isSuccess());
        result.setRefundReason(response.getRefundReason());
        result.setRefundTime(response.getGmtRefundPay());
        checkRefundQuerySubCode(result, response);
        return result;
    }

    /**
     * 统一收单交易支付入参校验
     *
     * @return
     */
    private String checkTradePayParam(AliPayTradePayParam param) {
        String message;
        if (StringUtils.isEmpty(param.getOutTradeNo())) {
            message = "商户订单号为空";
        } else if (StringUtils.isEmpty(param.getAuthCode())) {
            message = "授权码为空";
        } else if (StringUtils.isEmpty(param.getSubject())) {
            message = "订单标题为空";
        } else if (0 >= param.getTotalAmount()) {
            message = "金额必须大于0";
        } else if (StringUtils.isEmpty(param.getScene())) {
            message = "支付场景为空";
        } else {
            message = StringUtils.EMPTY;
        }
        return message;
    }

    /**
     * 查询接口校验subCode是否存在。如果subCode存在，代表调用接口失败。
     * 业务调用时只需判断result是否为success即可
     *
     * @return
     */
    private void checkTradeQuerySubCode(AliPayQueryResult result, AlipayTradeQueryResponse response) {
        if (StringUtils.isNotEmpty(response.getSubCode())) {
            result.setCode(response.getSubCode());
            result.setMessage(response.getSubMsg());
        } else {
            result.setCode(response.getCode());
            result.setMessage(response.getMsg());
        }
    }

    private void checkRefundQuerySubCode(AliPayRefundQueryResult result, AlipayTradeFastpayRefundQueryResponse response) {
        if (StringUtils.isNotEmpty(response.getSubCode())) {
            result.setCode(response.getSubCode());
            result.setMessage(response.getSubMsg());
        } else {
            result.setCode(response.getCode());
            result.setMessage(response.getMsg());
        }
    }

    public static void main(String[] args) {
        //刷卡支付
        AliPayService alipayBuilder = new AliPayService();
        String orderNo = "201805180207";//订单号
        String authCode = "285620814798006808";//二维码
        AliPayTradePayParam codePayParam = new AliPayTradePayParam(orderNo, authCode, "test", "test", 0.1f);
        AliPayTradePayResult codePayResult = alipayBuilder.tradePay(codePayParam);
        boolean success = codePayResult.isSuccess();
        if (!success) {
            System.out.println(codePayResult.getMessage());
        }

        //查询订单
        String tradeNo = codePayResult.getTradeNo();//支付宝交易号
        AliPayQueryResult queryResult = alipayBuilder.tradeQuery("", tradeNo);
        if (!queryResult.isSuccess()) {
            System.out.println(queryResult.getMessage());
        } else {
            String tradeStatus = queryResult.getTradeStatus();
            Optional<AliPayTradeStatusEnum> first = Arrays.stream(AliPayTradeStatusEnum.values()).filter(aliPayTradeStatusEnum -> aliPayTradeStatusEnum.getCode().equals(tradeStatus)).findFirst();
            first.ifPresent(aliPayTradeStatusEnum -> System.out.println(aliPayTradeStatusEnum.getName()));
        }

//        //退款操作
        String outRequestNo = "201805180202";//退款请求号
        AliPayTradeRefundParam refundParam = new AliPayTradeRefundParam(outRequestNo, tradeNo, "test", 0.05f);
        AliPayTradeRefundResult refundResult = alipayBuilder.tradeRefund(refundParam);
        System.out.println(refundResult.getMessage());

//        //查询退款 只要success,即为成功
        AliPayRefundQueryResult refund = alipayBuilder.refundQuery("", tradeNo, outRequestNo);
        System.out.println(refund.isSuccess());
    }
}
