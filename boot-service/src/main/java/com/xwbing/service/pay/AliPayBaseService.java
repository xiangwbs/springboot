package com.xwbing.service.pay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;

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
    @Value("${aliPay.userId}")
    public String aliPayUserId;
    @Value("${aliPay.serverUrl}")
    private String serverUrl;
    @Value("${aliPay.certificatePath}")
    private String certificatePath;
    /**
     * 支付宝分配给开发者的应用ID
     */
    @Value("${aliPay.appId}")
    private String appId;
    /**
     * 应用私钥
     */
    @Value("${aliPay.appPrivateKey}")
    private String appPrivateKey;
    /**
     * 支付宝公钥
     */
    @Value("${aliPay.aliPayPublicKey}")
    private String aliPayPublicKey;
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
}