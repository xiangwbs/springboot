package com.xwbing.domain.entity.pay.alipay;

import com.alipay.api.domain.TradeFundBill;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 说明: 统一收单交易支付结果
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:36
 * 作者:  xiangwb
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AliPayTradePayResult extends AliPayBaseResult {
    /**
     * 支付宝交易号
     */
    private String tradeNo;
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 买家支付宝账号
     */
    private String buyerLogonId;
    /**
     * 交易金额
     */
    private String totalAmount;
    /**
     * 实收金额
     */
    private String receiptAmount;
    /**
     * 交易支付时间
     */
    private Date gmtPayment;
    /**
     * 交易支付使用的资金渠道
     */
    private List<TradeFundBill> fundBillList;
    /**
     * 买家在支付宝的用户id
     */
    private String buyerUserId;

    public AliPayTradePayResult(boolean success) {
        this.setSuccess(success);
    }
}