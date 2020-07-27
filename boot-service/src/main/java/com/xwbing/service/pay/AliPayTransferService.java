package com.xwbing.service.pay;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayFundAccountQueryModel;
import com.alipay.api.domain.AlipayFundTransCommonQueryModel;
import com.alipay.api.domain.AlipayFundTransUniTransferModel;
import com.alipay.api.domain.Participant;
import com.alipay.api.request.AlipayFundAccountQueryRequest;
import com.alipay.api.request.AlipayFundTransCommonQueryRequest;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.response.AlipayFundAccountQueryResponse;
import com.alipay.api.response.AlipayFundTransCommonQueryResponse;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
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
    @Value("${aliPay.userId:}")
    private String aliPayUserId;
    @Resource
    private TradeRecordService tradeRecordService;
    @Resource
    private AlipayClient aliPayCertClient;

    /**
     * 查询支付宝账户余额
     */
    public BigDecimal accountQuery() {
        try {
            log.info("accountQuery start");
            AlipayFundAccountQueryRequest request = new AlipayFundAccountQueryRequest();
            AlipayFundAccountQueryModel model = new AlipayFundAccountQueryModel();
            model.setAlipayUserId(aliPayUserId);
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
        return null;
    }

    /**
     * 转账
     *
     * @param payAccount
     * @param name
     * @param orderId
     * @param amount
     * @param title
     *
     * @return
     */
    public boolean doTransfer(String payAccount, String name, String orderId, BigDecimal amount, String title) {
        //转账
        Boolean transfer = transfer(payAccount, name, orderId, amount, title);
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
            transfer = transfer(payAccount, name, orderId, amount, title);
            if (transfer != null) {
                return transfer;
            } else {
                //支付宝接口异常
                tradeRecordService.updateFail(orderId, null, "20000", "服务不可用", "unknow-error", "服务暂不可用", null, null);
                return false;
            }
        }
    }

    /**
     * 向指定支付宝账户转账
     *
     * @param payAccount 支持邮箱和手机号格式
     * @param name 参与方真实姓名
     * @param orderId 商户订单号
     * @param amount 订单总金额，单位为元，精确到小数点后两位 [0.1,100000000]
     * @param title 转账业务的标题
     *
     * @return
     */
    private Boolean transfer(String payAccount, String name, String orderId, BigDecimal amount, String title) {
        try {
            AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
            AlipayFundTransUniTransferModel model = new AlipayFundTransUniTransferModel();
            model.setOutBizNo(orderId);
            model.setTransAmount(amount.toString());
            model.setProductCode("TRANS_ACCOUNT_NO_PWD");
            model.setBizScene("DIRECT_TRANSFER");
            model.setOrderTitle(title);
            //收款方信息
            Participant participant = new Participant();
            participant.setIdentityType("ALIPAY_LOGON_ID");
            participant.setIdentity(payAccount);
            participant.setName(name);
            model.setPayeeInfo(participant);
            request.setBizModel(model);
            log.info("transfer orderId:{} request:{}", orderId, JSONObject.toJSONString(request));
            AlipayFundTransUniTransferResponse response = aliPayCertClient.certificateExecute(request);
            log.info("transfer orderId:{} response:{}", orderId, JSONObject.toJSONString(response));
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
            log.error("transfer orderId:{} amount:{} error", orderId, amount, e);
            return null;
        }
    }

    /**
     * 查询转账结果
     *
     * @param orderId 商户转账唯一订单号
     *
     * @return
     */
    private AlipayFundTransCommonQueryResponse transferQuery(String orderId) {
        try {
            AlipayFundTransCommonQueryRequest request = new AlipayFundTransCommonQueryRequest();
            AlipayFundTransCommonQueryModel model = new AlipayFundTransCommonQueryModel();
            //销售产品码 单笔无密转账到支付宝账户
            model.setProductCode("TRANS_ACCOUNT_NO_PWD");
            //业务场景 B2C现金红包、单笔无密转账
            model.setBizScene("DIRECT_TRANSFER");
            model.setOutBizNo(orderId);
            request.setBizModel(model);
            log.info("transferQuery orderId:{}", orderId);
            AlipayFundTransCommonQueryResponse response = aliPayCertClient.certificateExecute(request);
            log.info("transferQuery orderId:{} response:{}", orderId, JSONObject.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("transferQuery orderId:{} error", orderId, e);
            return null;
        }
    }
}
