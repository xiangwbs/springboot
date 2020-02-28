package com.xwbing.service.rest;

import com.xwbing.constant.CommonConstant;
import com.xwbing.domain.entity.pay.wxpay.*;
import com.xwbing.exception.BusinessException;
import com.xwbing.exception.PayException;
import com.xwbing.util.wxpay.ClientCustomSSL;
import com.xwbing.util.wxpay.RandomKit;
import com.xwbing.util.wxpay.WxSignKit;
import com.xwbing.util.wxpay.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 说明: 微信支付接口实现
 * 创建时间: 2017/5/10 17:30
 * 作者:  xiangwb
 */
@Slf4j
@Service
@PropertySource("classpath:pay.properties")
public class WxPayService {
    /**
     * 商户支付密钥 生成签名时用
     */
    @Value("${wx.apiKey}")
    private String apiKey;
    /**
     * 公众账号ID(企业号corpid)
     */
    @Value("${wx.appId}")
    private String appId;
    /**
     * 商户号
     */
    @Value("${wx.mchId}")
    private String mchId;
    /**
     * 刷卡支付url
     */
    @Value("${wx.barCodePayUrl}")
    private String barCodePayUrl;
    /**
     * 申请退款url
     */
    @Value("${wx.refundUrl}")
    private String refundUrl;
    /**
     * 查询订单url
     */
    @Value("${wx.orderQueryUrl}")
    private String orderQueryUrl;
    /**
     * 退款查询url
     */
    @Value("${wx.refundQueryUrl}")
    private String refundQueryUrl;

    /**
     * 条形码扫码付
     *
     * @param param
     * @return
     */

    public WxBarCodePayResult barCodePay(WxBarCodePayParam param) {
        WxBarCodePayResult result = new WxBarCodePayResult(false);
        //输入参数转为strXml
        String reqBody = buildBarCodeRequestBody(param);
        ///获取请求结果
        Map<String, String> resultMap = getResult(barCodePayUrl, reqBody, "扫码支付异常");
        if (!resultMap.isEmpty()) {
            //返回状态码SUCCESS/FAIL
            String returnCode = resultMap.get("return_code");
            result.setResultCode(returnCode);
            if (CommonConstant.FAIL.equalsIgnoreCase(returnCode)) {
                log.error("wx barCodePay failed!");
                //返回信息:非空,为错误原因
                result.setMessage(resultMap.get("return_msg"));
                return result;
            }
            log.info("outRefundNo=" + param.getOutTradeNo() + ",err_code=" + resultMap.get("err_code") + ",result_code=" + resultMap.get("result_code") + ",err_code_des=" + resultMap.get("err_code_des"));
            //业务结果SUCCESS/FAIL
            if (CommonConstant.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))) {
                result.setSuccess(true);
                result.setAppid(resultMap.get("appid"));
                result.setMchId(resultMap.get("mch_id"));
                result.setNonceStr(resultMap.get("nonce_str"));
                result.setSign(resultMap.get("sign"));
                result.setOpenId(resultMap.get("openid"));
                result.setIsSubscribe(resultMap.get("is_subscribe"));
                result.setTradeType(resultMap.get("trade_type"));
                result.setBankType(resultMap.get("bank_type"));
                result.setTotalFee(Integer.valueOf(resultMap.get("total_fee")));
                result.setCashFee(Integer.valueOf(resultMap.get("cash_fee")));
                result.setTransactionId(resultMap.get("transaction_id"));
                result.setOutTradeNo(resultMap.get("out_trade_no"));
                result.setTimeEnd(resultMap.get("time_end"));
            } else {
                result.setResultCode(resultMap.get("err_code"));
                result.setMessage(resultMap.get("err_code_des"));
            }
        } else {
            result.setMessage("error response");
        }
        return result;
    }

    /**
     * 退款
     *
     * @param param
     * @return
     */
    public WxRefundResult refund(WxRefundParam param) {
        WxRefundResult result = new WxRefundResult(false);
        String reqBody = buildRefundBarCodeRequestBody(param);
        Map<String, String> resultMap = getResult(refundUrl, reqBody, "退款异常");
        if (!resultMap.isEmpty()) {
            String returnCode = resultMap.get("return_code");
            result.setResultCode(returnCode);
            if (CommonConstant.FAIL.equalsIgnoreCase(returnCode)) {
                log.error("wx barCodePay failed!");
                result.setMessage(resultMap.get("return_msg"));
                return result;
            }
            log.info("outRefundNo=" + param.getOutTradeNo() + ",err_code=" + resultMap.get("err_code") + ",result_code=" + resultMap.get("result_code") + ",err_code_des=" + resultMap.get("err_code_des"));
            if (CommonConstant.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))) {
                result.setSuccess(true);
                result.setAppid(resultMap.get("appid"));
                result.setMchId(resultMap.get("mch_id"));
                result.setNonceStr(resultMap.get("nonce_str"));
                result.setSign(resultMap.get("sign"));
                result.setTransactionId(resultMap.get("transaction_id"));
                result.setOutTradeNo(resultMap.get("out_trade_no"));
                result.setOutRefundNo(resultMap.get("out_refund_no"));
                result.setRefundId(resultMap.get("refund_id"));
                result.setRefundFee(Integer.valueOf(resultMap.get("refund_fee")));
                result.setTotalFee(Integer.valueOf(resultMap.get("total_fee")));
                result.setCashFee(Integer.valueOf(resultMap.get("cash_fee")));
            } else {
                result.setResultCode(resultMap.get("err_code"));
                result.setMessage(resultMap.get("err_code_des"));
            }
        } else {
            result.setMessage("error response");
        }
        return result;
    }

    /**
     * 查询订单状态
     * 根据订单号 交易号查询 只需要一个即可
     * 如果isSuccess，根据tradeStatus，遍历WxTradeStatusEnum获取交易支付状态
     *
     * @param outTradeNo    商户订单号
     * @param transactionId 微信的订单号(推荐)
     * @return
     */
    public WxQueryResult orderQuery(String outTradeNo, String transactionId) {
        WxQueryResult result = new WxQueryResult(false);
        String reqBody = buildQueryRequestBody(outTradeNo, transactionId);
        Map<String, String> resultMap = getResult(orderQueryUrl, reqBody, "查询订单异常");
        if (!resultMap.isEmpty()) {
            String returnCode = resultMap.get("return_code");
            result.setResultCode(returnCode);
            if (CommonConstant.FAIL.equalsIgnoreCase(returnCode)) {
                log.error("wx barCodePay failed!");
                result.setMessage(resultMap.get("return_msg"));
                return result;
            }
            log.info("outTradeNo=" + outTradeNo + ",transactionId=" + transactionId);
            if (CommonConstant.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))) {
                result.setSuccess(true);
                //交易状态
                result.setTradeStatus(resultMap.get("trade_state"));
                return result;
            } else {
                result.setResultCode(resultMap.get("err_code"));
                result.setMessage(resultMap.get("err_code_des"));
            }
        } else {
            result.setMessage("error response");
        }
        return result;
    }

    /**
     * 查询退款
     * 参数四选一即可
     * 如果isSuccess，根据refundStatus，遍历WxRefundStatusEnum获取退款状态
     *
     * @param outTradeNo    商户订单号
     * @param transactionId 微信的订单号
     * @param ouRefundNo    商户退款单号(推荐)
     * @param refundId      微信退款单号(推荐)
     * @return
     */
    public WxQueryResult refundQuery(String outTradeNo, String transactionId, String ouRefundNo, String refundId) {
        WxQueryResult result = new WxQueryResult(false);
        String reqBody = buildRefundQueryRequestBody(outTradeNo, transactionId, ouRefundNo, refundId);
        Map<String, String> resultMap = getResult(refundQueryUrl, reqBody, "查询退款异常");
        if (!resultMap.isEmpty()) {
            String returnCode = resultMap.get("return_code");
            result.setResultCode(returnCode);
            if (CommonConstant.FAIL.equalsIgnoreCase(returnCode)) {
                log.error("wx barCodePay failed!");
                result.setMessage(resultMap.get("return_msg"));
                return result;
            }
            log.info("outTradeNo=" + outTradeNo + ",transactionId=" + transactionId + "ouRefundNo=" + ouRefundNo + "refundId=" + refundId);
            //业务结果
            if (CommonConstant.SUCCESS.equalsIgnoreCase(resultMap.get("result_code"))) {
                result.setSuccess(true);
                //第一笔退款状态
                result.setRefundStatus(resultMap.get("refund_status_0"));
                return result;
            } else {
                result.setResultCode(resultMap.get("err_code"));
                result.setMessage(resultMap.get("err_code_des"));
            }
        } else {
            result.setMessage("error response");
        }
        return result;
    }

    /**
     * 获取请求结果
     *
     * @param url     对应操作url
     * @param reqBody 参数
     * @param message 异常时抛出的信息
     * @return
     */
    private Map<String, String> getResult(String url, String reqBody, String message) {
        HttpPost post = new HttpPost(url);
        post.setEntity(new StringEntity(reqBody, "UTF-8"));
        //根据mchId读取微信证书,SSL创建安全连接
        CloseableHttpClient httpclient = ClientCustomSSL.getCloseableHttpClient(mchId);
        try {
            CloseableHttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                //返回结果为strXml
                String content = EntityUtils.toString(entity, "UTF-8");
                //解析返回值
                return XmlUtil.doXMLParse(content);
            } else {
                return Collections.EMPTY_MAP;
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new PayException(message);
        }
    }

    /**
     * 构建扫码参数
     *
     * @param param 微信扫码支付接口参数实体
     * @return
     */
    private String buildBarCodeRequestBody(WxBarCodePayParam param) {
        //获取基本参数
        Map<String, String> params = buildBaseBody();
        params.put("body", param.getBody());
        params.put("out_trade_no", param.getOutTradeNo());
        //单位分
        params.put("total_fee", String.valueOf(param.getTotalFee()));
        params.put("spbill_create_ip", param.getSpblillCreateIp());
        params.put("auth_code", param.getAuthCode());
        //获取最终参数
        return buildResultBody(params);
    }

    /**
     * 构建退款参数
     *
     * @param param 微信退款参数实体
     * @return
     */
    private String buildRefundBarCodeRequestBody(WxRefundParam param) {
        Map<String, String> params = buildBaseBody();
        //商户订单号
        String transactionId = param.getTransactionId();
        //微信订单号
        String outTradeNo = param.getOutTradeNo();
        if (StringUtils.isEmpty(transactionId) && StringUtils.isEmpty(outTradeNo)) {
            throw new PayException("商户订单号和微信订单号不能同时为空!");
        }
        if (StringUtils.isNotEmpty(transactionId)) {
            params.put("transaction_id", transactionId);
        }
        if (StringUtils.isNotEmpty(outTradeNo)) {
            params.put("out_trade_no", outTradeNo);
        }
        params.put("out_refund_no", param.getOutRefundNo());
        params.put("total_fee", String.valueOf(param.getTotalFee()));
        params.put("refund_fee", String.valueOf(param.getRefundFee()));
        params.put("op_user_id", param.getOpUserId());
        return buildResultBody(params);
    }

    /**
     * 构建订单查询参数
     *
     * @param outTradeNo    商户订单号
     * @param transactionId 微信订单号
     * @return
     */
    private String buildQueryRequestBody(String outTradeNo, String transactionId) {
        Map<String, String> params = buildBaseBody();
        if (StringUtils.isEmpty(transactionId) && StringUtils.isEmpty(outTradeNo)) {
            throw new PayException("商户订单号和微信订单号不能同时为空!");
        }
        if (StringUtils.isNotEmpty(transactionId)) {
            params.put("transaction_id", transactionId);
        }
        if (StringUtils.isNotEmpty(outTradeNo)) {
            params.put("out_trade_no", outTradeNo);
        }
        return buildResultBody(params);
    }

    /**
     * 构建退款查询参数
     *
     * @param outTradeNo    商户订单号
     * @param transactionId 微信订单号
     * @param ouRefundNo    商户退款单号
     * @param refundId      微信退款单号
     * @return
     */
    private String buildRefundQueryRequestBody(String outTradeNo, String transactionId, String ouRefundNo, String refundId) {
        Map<String, String> params = buildBaseBody();
        if (StringUtils.isEmpty(transactionId) && StringUtils.isEmpty(outTradeNo) && StringUtils.isEmpty(ouRefundNo) && StringUtils.isEmpty(refundId)) {
            throw new PayException("商户订单号,微信订单号,商户退款单号和微信退款单号不能同时为空!");
        }
        if (StringUtils.isNotEmpty(transactionId)) {
            params.put("transaction_id", transactionId);
        }
        if (StringUtils.isNotEmpty(outTradeNo)) {
            params.put("out_trade_no", outTradeNo);
        }
        if (StringUtils.isNotEmpty(ouRefundNo)) {
            params.put("out_refund_no", ouRefundNo);
        }
        if (StringUtils.isNotEmpty(refundId)) {
            params.put("refund_id", refundId);
        }
        return buildResultBody(params);
    }

    /**
     * 构建基本参数
     *
     * @return
     */
    private Map<String, String> buildBaseBody() {
        Map<String, String> params = new HashMap<>(20);
        params.put("appid", appId);
        params.put("mch_id", mchId);
        String nonceStr = RandomKit.buildRandom(32);
        params.put("nonce_str", nonceStr);
        return params;
    }

    /**
     * 构建最终xmlStr参数
     *
     * @return
     */
    private String buildResultBody(Map<String, String> params) {
        //签名放最后的
        String sign = WxSignKit.buildSign(params, apiKey);
        params.put("sign", sign);
        StringBuilder reqBody = new StringBuilder();
        reqBody.append("<xml>");
        for (String key : params.keySet()) {
            String value = params.get(key);
            reqBody.append("<").append(key).append(">").append(value).append("</").append(key).append(">");
        }
        reqBody.append("</xml>");
        return reqBody.toString();
    }

    public static void main(String[] args) {
        WxPayService wxPayBuilder = new WxPayService();
        //刷卡支付
        String authCode = "130203463134616871";//二维码
        String orderNo = "2017051200";//订单号
        WxBarCodePayParam payParam = new WxBarCodePayParam(orderNo, "127.0.0.0", authCode, "test", 1);
        WxBarCodePayResult result = wxPayBuilder.barCodePay(payParam);
        System.out.println(result.isSuccess() + result.getMessage());

        //查询订单
        String transactionId = result.getTransactionId();//微信的订单号
        WxQueryResult queryResult = wxPayBuilder.orderQuery("", transactionId);
        if (!queryResult.isSuccess()) {
            throw new BusinessException(queryResult.getMessage());
        }
        String tradeStatus = queryResult.getTradeStatus();
        Optional<WxTradeStatusEnum> wxTrade = Arrays.stream(WxTradeStatusEnum.values()).filter(wxTradeStatusEnum -> wxTradeStatusEnum.getCode().equals(tradeStatus)).findFirst();
        wxTrade.ifPresent(wxTradeStatusEnum -> System.out.println(wxTradeStatusEnum.getName()));

        //退款操作
        String outRefundNo = "2017051201";//商户退款单号
        WxRefundParam param = new WxRefundParam(transactionId, outRefundNo, "xwbing", 1, 1);
        WxRefundResult refundResult = wxPayBuilder.refund(param);
        System.out.println(refundResult.isSuccess() + refundResult.getMessage());

        //查询退款
        WxQueryResult refundQueryResult = wxPayBuilder.refundQuery("", "", "", refundResult.getRefundId());
        if (!refundQueryResult.isSuccess()) {
            throw new BusinessException(refundQueryResult.getMessage());
        }
        String refundStatus = refundQueryResult.getRefundStatus();
        Optional<WxRefundStatusEnum> wxRefund = Arrays.stream(WxRefundStatusEnum.values()).filter(wxRefundStatusEnum -> wxRefundStatusEnum.getCode().equals(refundStatus)).findFirst();
        wxRefund.ifPresent(wxRefundStatusEnum -> System.out.println(wxRefundStatusEnum.getName()));
    }
}
