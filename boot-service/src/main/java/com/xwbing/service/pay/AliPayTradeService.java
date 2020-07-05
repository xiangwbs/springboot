package com.xwbing.service.pay;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.xwbing.exception.PayException;
import com.xwbing.service.pay.vo.AliPayRefundQueryResult;
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
 * 当面付
 *
 * @author xwbing
 */
@Slf4j
@Service
@PropertySource("classpath:pay.properties")
public class AliPayTradeService extends AliPayBaseService {
    @Value("${aliPay.tradePay.notifyUrl:}")
    private String notifyUrl;

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
            AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
            //异步回调通知地址
            if (StringUtils.isNotEmpty(notifyUrl)) {
                request.setNotifyUrl(notifyUrl + "/payNotice/aliPay/tradeCreate");
            }
            request.setBizContent(JSONObject.toJSONString(param));
            log.info("tradeCreate outTradeNo:{} request:{}", outTradeNo, JSONObject.toJSONString(request));
            AlipayTradeCreateResponse response = getAliPayClient().execute(request);
            log.info("tradeCreate outTradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            return response.isSuccess() ?
                    AliPayTradeCreateResult.ofSuccess(response) :
                    AliPayTradeCreateResult.ofFail(response);
        } catch (Exception e) {
            log.error("tradeCreate outTradeNo:{} error", outTradeNo, e);
            return AliPayTradeCreateResult.ofError();
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
            AlipayTradePayRequest request = new AlipayTradePayRequest();
            if (StringUtils.isNotEmpty(notifyUrl)) {
                request.setNotifyUrl(notifyUrl + "/payNotice/aliPay/tradePay");
            }
            request.setBizContent(JSONObject.toJSONString(param));
            AlipayTradePayResponse response = getAliPayClient().execute(request);
            log.info("tradePay outTradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            return response.isSuccess() ?
                    AliPayTradePayResult.ofSuccess(response) :
                    AliPayTradePayResult.ofFail(response);
        } catch (Exception e) {
            log.error("aliPayTradePay outTradeNo:{} error", outTradeNo, e);
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
            log.info("wapPay outTradeNo:{} param:{}", outTradeNo, JSONObject.toJSONString(param));
            AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
            if (StringUtils.isNotEmpty(notifyUrl)) {
                request.setNotifyUrl(notifyUrl + "/payNotice/aliPay/tradePay");
            }
            if (StringUtils.isNotEmpty(param.getReturnUrl())) {
                request.setReturnUrl(param.getReturnUrl());
            }
            request.setBizContent(JSONObject.toJSONString(param));
            AlipayTradeWapPayResponse response = getAliPayClient().pageExecute(request);
            log.info("wapPay outTradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(response));
            String form = response.getBody();
            httpResponse.setContentType("text/html;charset=utf-8");
            //直接将完整的表单html输出到页面
            httpResponse.getWriter().write(form);
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        } catch (Exception e) {
            log.error("wapPay outTradeNo:{} error", outTradeNo, e);
            throw new PayException("手机网站支付支付异常");
        }
    }

    // public void pagePay(HttpServletResponse httpResponse) {
    //     try {
    //         AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
    //         alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
    //         alipayRequest.setNotifyUrl("http://domain.com/CallBack/notify_url.jsp");
    //         alipayRequest.putOtherTextParam("app_auth_token",
    //                 "201611BB8xxxxxxxxxxxxxxxxxxxedcecde6");//如果 ISV 代商家接入电脑网站支付能力，则需要传入 app_auth_token，使用第三方应用授权；自研开发模式请忽略
    //         alipayRequest.setBizContent("{" + "    \"out_trade_no\":\"20150320010101001\","
    //                 + "    \"product_code\":\"FAST_INSTANT_TRADE_PAY\"," + "    \"total_amount\":88.88,"
    //                 + "    \"subject\":\"Iphone6 16G\"," + "    \"body\":\"Iphone6 16G\","
    //                 + "    \"passback_params\":\"merchantBizType%3d3C%26merchantBizNo%3d2016010101111\","
    //                 + "    \"extend_params\":{" + "    \"sys_service_provider_id\":\"2088511833207846\"" + "    }"
    //                 + "  }"); //填充业务参数
    //         String form = "";
    //         try {
    //             form = getAliPayClient().pageExecute(alipayRequest).getBody();  //调用SDK生成表单
    //         } catch (AlipayApiException e) {
    //             e.printStackTrace();
    //         }
    //         httpResponse.setContentType("text/html;charset=utf-8");
    //         //直接将完整的表单html输出到页面
    //         httpResponse.getWriter().write(form);
    //         httpResponse.getWriter().flush();
    //         httpResponse.getWriter().close();
    //     } catch (Exception e) {
    //
    //     }
    // }

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
            return response.isSuccess() ?
                    AliPayTradeQueryResult.ofSuccess(response) :
                    AliPayTradeQueryResult.ofFail(response);
        } catch (Exception e) {
            log.error("tradeQuery outTradeNo:{} tradeNo:{} error", outTradeNo, tradeNo, e);
            return AliPayTradeQueryResult.ofError();
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
            BigDecimal refundAmount = request.getRefundAmount();
            if (refundAmount == null) {
                throw new PayException("退款金额不能为空");
            }
            if (refundAmount.compareTo(BigDecimal.ZERO) < 1) {
                throw new PayException("退款金额不能为小于0");
            }
            AlipayTradeRefundRequest refundRequest = new AlipayTradeRefundRequest();
            refundRequest.setBizContent(JSONObject.toJSONString(request));
            AlipayTradeRefundResponse response = getAliPayClient().execute(refundRequest);
            log.info("tradeRefund outTradeNo:{} response:{}", outTradeNo, JSONObject.toJSONString(request),
                    JSONObject.toJSONString(response));
            return response.isSuccess() && "Y".equals(response.getFundChange()) ?
                    AliPayTradeRefundResult.ofSuccess(response) :
                    AliPayTradeRefundResult.ofFail(response);
        } catch (Exception e) {
            log.error("tradeRefund outTradeNo:{} error", outTradeNo, JSONObject.toJSONString(request), e);
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
            return response.isSuccess() ?
                    AliPayRefundQueryResult.ofSuccess(response) :
                    AliPayRefundQueryResult.ofFail(response);
        } catch (Exception e) {
            log.error("refundQuery outRequestNo:{} error", outRequestNo, e);
            return AliPayRefundQueryResult.ofError();
        }
    }
}
