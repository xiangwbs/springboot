package com.xwbing.service.pay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayFundAccountQueryRequest;
import com.alipay.api.request.AlipayFundTransCommonQueryRequest;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayFundAccountQueryResponse;
import com.alipay.api.response.AlipayFundTransCommonQueryResponse;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.xwbing.exception.BusinessException;
import com.xwbing.service.pay.enums.TransferStatusEnum;
import com.xwbing.util.DateUtil2;

import lombok.extern.slf4j.Slf4j;

/**
 * 转账到支付宝账户接口
 *
 * 转账额度
 * 单笔限额：转账给个人支付宝账户，单笔最高 5 万元；转账给企业支付宝账户，单笔最高 10 万元。
 * 日限额：初始额度为 200 万元，即每日最高可转200万元。
 * 月限额：初始额度为 3100 万元，即每月最高可转3100万元。
 *
 * 相关文档
 * https://opendocs.alipay.com/open/309/106235
 * https://opensupport.alipay.com/support/helpcenter/107/201602484934#?ant_source=manual&recommend=84921dbf0458195602513447ca3ca661
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年04月29日 下午12:08
 */
@Slf4j
@Service
public class AliPayTransferService {
    @Value("${aliPay.serverUrl}")
    private String serverUrl;
    @Value("${aliPay.certificatePath}")
    private String certificatePath;
    @Value("${aliPay.appId}")
    private String appId;
    @Value("${aliPay.userId}")
    private String aliPayUserId;
    @Value("${aliPay.rsaPrivateKey}")
    private String privateKey;
    private volatile AlipayClient alipayClient;
    private final TradeRecordService tradeRecordService;

    public AliPayTransferService(TradeRecordService tradeRecordService) {
        this.tradeRecordService = tradeRecordService;
    }

    /**
     * 查询支付宝账户余额
     */
    public BigDecimal accountQuery() {
        try {
            AlipayFundAccountQueryRequest request = new AlipayFundAccountQueryRequest();
            Map<String, Object> bizContent = new HashMap<>(2);
            bizContent.put("alipay_user_id", aliPayUserId);
            //查询的账号类型 查询余额账户
            bizContent.put("account_type", "ACCTRANS_ACCOUNT");
            request.setBizContent(JSONObject.toJSONString(bizContent));
            AlipayFundAccountQueryResponse response = getAliPayCertClient().certificateExecute(request);
            log.error("accountQuery response:{}", JSONObject.toJSONString(response));
            if (response.isSuccess()) {
                return new BigDecimal(response.getAvailableAmount());
            } else {
                throw new BusinessException("查询支付宝账户余额异常");
            }
        } catch (Exception e) {
            log.error("accountQuery error", e);
            throw new BusinessException("查询支付宝账户余额异常");
        }
    }

    /**
     * 向指定支付宝账户转账
     *
     * @param payAccount
     * @param name
     * @param orderId
     * @param amount [0.1,100000000]
     * @param subject
     *
     * @return
     */
    public boolean doTransfer(String payAccount, String name, String orderId, BigDecimal amount, String subject) {
        //转账
        Boolean transfer = transfer(payAccount, name, orderId, amount, subject);
        if (transfer != null) {
            return transfer;
        }
        //查询转账信息
        AlipayFundTransCommonQueryResponse response = transferQuery(orderId);
        //支付宝接口异常
        if (response == null) {
            tradeRecordService.updateFail(orderId, null, "20000", "服务不可用", "unknow-error", "服务暂不可用", null, null);
            return false;
        }
        //据响应参数status（转账单据状态）、pay_date（支付时间）参数判断
        if (response.isSuccess() && TransferStatusEnum.SUCCESS.getCode().equals(response.getStatus()) && StringUtils
                .isNotEmpty(response.getPayDate())) {
            tradeRecordService.updateSuccess(orderId, response.getOutBizNo(),
                    DateUtil2.strToDate(response.getPayDate(), DateUtil2.YYYY_MM_DD_HH_MM_SS), null, null);
            return true;
        } else {
            //再次转账
            transfer = transfer(payAccount, name, orderId, amount, subject);
            if (transfer != null) {
                return transfer;
            } else {
                //支付宝接口异常
                tradeRecordService.updateFail(orderId, null, "20000", "服务不可用", "unknow-error", "服务暂不可用", null, null);
                return false;
            }
        }
    }

    private Boolean transfer(String payAccount, String name, String orderId, BigDecimal amount, String subject) {
        try {
            AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
            Map<String, Object> bizContent = new HashMap<>(6);
            //商户订单号
            bizContent.put("out_biz_no", orderId);
            //订单总金额 单位为元 精确到小数点后两位
            bizContent.put("trans_amount", amount);
            //业务产品码 单笔无密转账到支付宝账户
            bizContent.put("product_code", "TRANS_ACCOUNT_NO_PWD");
            //业务场景 单笔无密转账到支付宝
            bizContent.put("biz_scene", "DIRECT_TRANSFER");
            //转账标题
            bizContent.put("order_title", subject);
            bizContent.put("remark", subject);
            //收款方信息
            Map<String, Object> payeeInfo = new HashMap<>(3);
            //参与方的标识类型 支付宝登录号 支持邮箱和手机号格式
            payeeInfo.put("identity_type", "ALIPAY_LOGON_ID");
            //参与方的唯一标识
            payeeInfo.put("identity", payAccount);
            //参与方真实姓名
            payeeInfo.put("name", name);
            bizContent.put("payee_info", payeeInfo);
            request.setBizContent(JSONObject.toJSONString(bizContent));
            log.info("transfer orderId={} request:{}", orderId, JSONObject.toJSONString(request));
            AlipayFundTransUniTransferResponse response = getAliPayCertClient().certificateExecute(request);
            log.info("transfer orderId={} response:{}", orderId, JSONObject.toJSONString(response));
            //转账失败接口会直接同步返回错误，只要判断status=SUCCESS即可，如果出现其他都是不成功的
            if (response.isSuccess() && TransferStatusEnum.SUCCESS.getCode().equals(response.getStatus())) {
                tradeRecordService.updateSuccess(orderId, response.getOrderId(),
                        DateUtil2.strToDate(response.getTransDate(), DateUtil2.YYYY_MM_DD_HH_MM_SS), null, null);
                return true;
            } else {
                tradeRecordService.updateFail(orderId, response.getOrderId(), response.getCode(), response.getMsg(),
                        response.getSubCode(), response.getSubMsg(), null, null);
                return false;
            }
        } catch (Exception e) {
            log.error("transfer orderId={} amount={} error", orderId, amount, e);
            return null;
        }
    }

    private AlipayFundTransCommonQueryResponse transferQuery(String orderId) {
        try {
            AlipayFundTransCommonQueryRequest request = new AlipayFundTransCommonQueryRequest();
            Map<String, Object> bizContent = new HashMap<>(3);
            //销售产品码 单笔无密转账到支付宝账户
            bizContent.put("product_code", "TRANS_ACCOUNT_NO_PWD");
            //业务场景 B2C现金红包、单笔无密转账
            bizContent.put("biz_scene", "DIRECT_TRANSFER");
            //商户转账唯一订单号
            bizContent.put("out_biz_no", orderId);
            request.setBizContent(JSONObject.toJSONString(bizContent));
            log.info("transferQuery orderId={}", orderId);
            AlipayFundTransCommonQueryResponse response = getAliPayCertClient().certificateExecute(request);
            log.info("transferQuery orderId={} response:{}", orderId, JSONObject.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("transferQuery orderId={} error", orderId, e);
            return null;
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
            Map<String, Object> bizContent = new HashMap<>(2);
            bizContent.put("bill_type", "signcustomer");
            bizContent.put("bill_date", billDate);
            request.setBizContent(JSONObject.toJSONString(bizContent));
            AlipayDataDataserviceBillDownloadurlQueryResponse response = getAliPayCertClient()
                    .certificateExecute(request);
            log.info("queryBillDownloadUrl response:{}", JSONObject.toJSONString(response));
            if (response.isSuccess()) {
                return response.getBillDownloadUrl();
            } else {
                log.error("queryBillDownloadUrl error:{}", response.getSubMsg());
                throw new BusinessException("查询对账单下载地址异常");
            }
        } catch (Exception e) {
            log.error("queryBillDownloadUrl error", e);
            throw new BusinessException("查询对账单下载地址异常");
        }
    }

    /**
     * 获取支付宝证书客户端
     * 避免无证书报错，采用懒加载
     *
     * @return
     */
    private AlipayClient getAliPayCertClient() {
        if (alipayClient == null) {
            synchronized (AliPayTransferService.class) {
                if (alipayClient == null) {
                    try {
                        CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
                        certAlipayRequest.setServerUrl(serverUrl);
                        certAlipayRequest.setAppId(appId);
                        certAlipayRequest.setPrivateKey(privateKey);
                        certAlipayRequest.setFormat("json");
                        certAlipayRequest.setCharset("UTF-8");
                        certAlipayRequest.setSignType("RSA2");
                        certAlipayRequest.setCertPath(certificatePath + "/appCertPublicKey.crt");
                        certAlipayRequest.setAlipayPublicCertPath(certificatePath + "/alipayCertPublicKey_RSA2.crt");
                        certAlipayRequest.setRootCertPath(certificatePath + "/alipayRootCert.crt");
                        alipayClient = new DefaultAlipayClient(certAlipayRequest);
                        log.info("initAliPayCertClient success");
                    } catch (Exception e) {
                        log.error("initAliPayCertClient error", e);
                    }
                }
            }
        }
        return alipayClient;
    }
}
