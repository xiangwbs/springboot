package com.xwbing.service.pay;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.xwbing.exception.PayException;
import com.xwbing.service.pay.vo.AliPayRefundQueryResult;
import com.xwbing.service.pay.vo.AliPayTradePayParam;
import com.xwbing.service.pay.vo.AliPayTradePayResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 说明: 支付宝扫码支付接口
 * 创建时间: 2017/5/10 17:50
 *
 * @author xwbing
 */
@Slf4j
@Service
@PropertySource("classpath:pay.properties")
public class AliPayBarService extends AliPayBaseService {
    @Value("${aliPay.tradePay.notifyUrl:}")
    private String notifyUrl;

    /**
     * 统一收单交易支付
     * 条码支付|声波支付
     *
     * @param param
     *
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
            response = getAliPayClient().execute(request);
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
     * 统一收单交易退款查询
     * 商户订单号和支付宝交易号2选1
     * 退款查询,没有tradeStatus。isSuccess即为成功
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
            log.info("refundQuery outRequestNo:{} request:{}", outRequestNo, JSONObject.toJSONString(request));
            AlipayTradeFastpayRefundQueryResponse response = getAliPayClient().execute(request);
            log.info("refundQuery outRequestNo:{} response:{}", outRequestNo, JSONObject.toJSONString(response));
            if (response.isSuccess()) {
                return AliPayRefundQueryResult.builder().success(true).refundStatus(response.getRefundStatus())
                        .totalAmount(new BigDecimal(response.getTotalAmount()))
                        .refundAmount(new BigDecimal(response.getRefundAmount()))
                        .refundReason(response.getRefundReason()).refundTime(response.getGmtRefundPay())
                        .code(response.getCode()).message(response.getMsg()).build();
            } else {
                return AliPayRefundQueryResult.builder().success(false).code(response.getSubCode())
                        .message(response.getSubMsg()).build();
            }
        } catch (Exception e) {
            log.error("refundQuery outRequestNo:{} error", outRequestNo, e);
            throw new PayException("统一收单交易退款查询异常");
        }
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

    public static void main(String[] args) {
        // ---------------------- 刷卡支付 ----------------------
        AliPayBarService alipayBuilder = new AliPayBarService();
        String orderNo = "201805180207";//订单号
        String authCode = "285620814798006808";//二维码
        String hbFqNum = "3";//花呗分期数
        AliPayTradePayParam codePayParam = new AliPayTradePayParam(orderNo, authCode, "test", null, 0.1f);
        JSONObject extendParams = new JSONObject();
        extendParams.put("hb_fq_num", hbFqNum);
        extendParams.put("hb_fq_seller_percent", "0");
        codePayParam.setExtendParams(extendParams);
        AliPayTradePayResult codePayResult = alipayBuilder.tradePay(codePayParam);
        boolean success = codePayResult.isSuccess();
        if (!success) {
            System.out.println(codePayResult.getMessage());
        }
    }
}
