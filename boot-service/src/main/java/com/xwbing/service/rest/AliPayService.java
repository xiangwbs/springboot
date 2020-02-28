package com.xwbing.service.rest;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.xwbing.domain.entity.pay.alipay.*;
import com.xwbing.exception.PayException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    /**
     * 请求url
     */
    @Value("${aliPay.requestUrl}")
    private String requestUrl;

    /**
     * 条形码扫码付
     *
     * @param param
     * @return
     */
    public AliPayBarCodePayResult barCodePay(AliPayBarCodePayParam param) {
        //设置条码支付
        param.setScene("bar_code");
        AliPayBarCodePayResult result = new AliPayBarCodePayResult(false);
        String checkArgument = checkArgument(param);
        if (StringUtils.isNotEmpty(checkArgument)) {
            result.setMessage(checkArgument);
            return result;
        }
        //获得初始化的aliPayClient
        AlipayClient alipayClient = new DefaultAlipayClient(requestUrl, appId, privateKey, "json", "UTF-8", publicKey, "RSA");
        //创建API对应的request类
        AlipayTradePayRequest request = new AlipayTradePayRequest();
        request.setBizContent(JSONObject.toJSONString(param));
        //通过aliPayClient调用API，获得对应的response类
        AlipayTradePayResponse response;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error(e.getMessage());
            throw new PayException("扫码支付异常");
        }
        //源码StringUtils.isEmpty(this.subCode)
        result.setSuccess(response.isSuccess());
        //根据response中的结果继续业务逻辑处理:subCode不为空，表示接口调用失败|code为10000，代表接口调用成功，subCode为空
        if (StringUtils.isEmpty(response.getSubCode())) {
            result.setCode(response.getCode());
            result.setMessage(response.getMsg());
        } else {
            result.setCode(response.getSubCode());
            result.setMessage(response.getSubMsg());
            log.error(response.getSubMsg());
            return result;
        }
        result.setTradeNo(response.getTradeNo());
        result.setOutTradeNo(response.getOutTradeNo());
        result.setBuyerLogonId(response.getBuyerLogonId());
        result.setTotalAmount(response.getTotalAmount());
        result.setReceiptAmount(response.getReceiptAmount());
        result.setGmtPayment(response.getGmtPayment());
        result.setFundBillList(response.getFundBillList());
        result.setBuyerUserId(response.getBuyerUserId());
        result.setDiscountGoodsDetail(response.getDiscountGoodsDetail());
        log.info("result = {}", result);
        return result;
    }

    /**
     * 退款
     *
     * @param param
     * @return
     */
    public AliPayRefundResult refund(AliPayRefundParam param) {
        AliPayRefundResult result = new AliPayRefundResult(false);
        AlipayClient alipayClient = new DefaultAlipayClient(requestUrl, appId, privateKey, "json", "UTF-8", publicKey, "RSA");
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent(JSONObject.toJSONString(param));
        AlipayTradeRefundResponse response;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error(e.getMessage());
            throw new PayException("退款异常");
        }
        result.setSuccess(response.isSuccess());
        if (StringUtils.isNotEmpty(response.getSubCode())) {
            log.error(response.getSubMsg());
            result.setCode(response.getSubCode());
            result.setMessage(response.getSubMsg());
            return result;
        } else {
            result.setCode(response.getCode());
            result.setMessage(response.getMsg());
        }
        result.setTradeNo(response.getTradeNo());
        result.setOutTradeNo(response.getOutTradeNo());
        result.setBuyerLogonId(response.getBuyerLogonId());
        result.setFundChange(response.getFundChange());
        result.setRefundFee(response.getRefundFee());
        result.setGmtRefundPay(response.getGmtRefundPay());
        result.setBuyerUserId(response.getBuyerUserId());
        log.info("result = {}", result);
        return result;
    }

    /**
     * 根据订单号 交易号查询 只需要一个即可
     * 如果isSuccess，根据tradeStatus，遍历AliPayTradeStatusEnum获取对应支付状态
     *
     * @param outTradeNo 订单号
     * @param tradeNo    交易号(推荐)
     * @return
     */
    public AliPayQueryResult queryOrder(String outTradeNo, String tradeNo) {
        if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(tradeNo)) {
            throw new PayException("订单号和交易号不能同时为空!");
        }
        AliPayQueryResult result = new AliPayQueryResult(false);
        AlipayClient alipayClient = new DefaultAlipayClient(requestUrl, appId, privateKey, "json", "UTF-8", publicKey, "RSA");
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
            log.error(e.getMessage());
            throw new PayException("查询订单异常");
        }
        result.setSuccess(response.isSuccess());
        //如果subCode存在,代表调用接口失败
        checkSubCode(result, response);
        //返回支付状态,业务调用时,遍历AliPayTradeStatusEnum与tradeStatus做比较
        result.setTradeStatus(response.getTradeStatus());
        return result;
    }

    /**
     * 退款查询,没有tradeStatus。isSuccess即为成功
     * 订单号和交易号2选1
     *
     * @param outTradeNo   订单号
     * @param tradeNo      交易号(推荐)
     * @param outRequestNo 退款请求号
     * @return
     */
    public AliPayQueryResult queryRefund(String outTradeNo, String tradeNo, String outRequestNo) {
        if (StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(tradeNo)) {
            throw new PayException("订单号和交易号不能同时为空!");
        }
        AliPayQueryResult result = new AliPayQueryResult(false);
        AlipayClient alipayClient = new DefaultAlipayClient(requestUrl, appId, privateKey, "json", "UTF-8", publicKey, "RSA");
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_request_no", outRequestNo);
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
            log.error(e.getMessage());
            throw new PayException("退款查询异常");
        }
        result.setSuccess(response.isSuccess());
        //如果subCode存在,代表调用接口失败
        checkSubCode(result, response);
        return result;
    }

    /**
     * 入参校验
     *
     * @return
     */
    private String checkArgument(AliPayBarCodePayParam param) {
        String message;
        if (StringUtils.isEmpty(param.getOutTradeNo())) {
            message = "订单号为空";
        } else if (StringUtils.isEmpty(param.getAuthCode())) {
            message = "授权码为空";
        } else if (StringUtils.isEmpty(param.getSubject())) {
            message = "产品名称为空";
        } else if (0 >= param.getTotalAmount()) {
            message = "金额必须大于0";
        } else if (StringUtils.isEmpty(param.getScene())) {
            message = "支付方式不能为空";
        } else {
            message = StringUtils.EMPTY;
        }
        return message;
    }

    /**
     * 查询接口校验subCode是否存在。如果subCode存在，代表调用接口失败。业务调用时只需判断result是否为success即可
     *
     * @return
     */
    private void checkSubCode(AliPayQueryResult result, AlipayTradeQueryResponse response) {
        if (StringUtils.isNotEmpty(response.getSubCode())) {
            result.setCode(response.getSubCode());
            result.setMessage(response.getSubMsg());
            log.error(response.getSubMsg());
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
        AliPayBarCodePayParam codePayParam = new AliPayBarCodePayParam(orderNo, authCode, "test", 0.1f);
        AliPayBarCodePayResult codePayResult = alipayBuilder.barCodePay(codePayParam);
        boolean success = codePayResult.isSuccess();
        if (!success) {
            System.out.println(codePayResult.getMessage());
        }

        //查询订单
        String tradeNo = codePayResult.getTradeNo();//支付宝交易号
        AliPayQueryResult queryResult = alipayBuilder.queryOrder("", tradeNo);
        if (!queryResult.isSuccess()) {
            System.out.println(queryResult.getMessage());
        } else {
            String tradeStatus = queryResult.getTradeStatus();
            Optional<AliPayTradeStatusEnum> first = Arrays.stream(AliPayTradeStatusEnum.values()).filter(aliPayTradeStatusEnum -> aliPayTradeStatusEnum.getCode().equals(tradeStatus)).findFirst();
            first.ifPresent(aliPayTradeStatusEnum -> System.out.println(aliPayTradeStatusEnum.getName()));
        }

//        //退款操作
        String outRequestNo = "201805180202";//退款请求号
        AliPayRefundParam refundParam = new AliPayRefundParam(outRequestNo, tradeNo, "test", 0.05f);
        AliPayRefundResult refundResult = alipayBuilder.refund(refundParam);
        System.out.println(refundResult.getMessage());

//        //查询退款 只要success,即为成功
        AliPayQueryResult refund = alipayBuilder.queryRefund("", tradeNo, outRequestNo);
        System.out.println(refund.isSuccess());
    }
}
