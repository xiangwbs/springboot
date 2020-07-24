package com.xwbing.service.pay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayDataDataserviceBillDownloadurlQueryModel;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;

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
        return alipayClient;
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
            AlipayDataDataserviceBillDownloadurlQueryModel model = new AlipayDataDataserviceBillDownloadurlQueryModel();
            model.setBillType("signcustomer");
            model.setBillDate(billDate);
            request.setBizModel(model);
            AlipayDataDataserviceBillDownloadurlQueryResponse response = getAliPayClient().execute(request);
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