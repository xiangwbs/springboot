package com.xwbing.service.pay.vo;

import java.util.Date;
import java.util.List;

import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradePayResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 统一收单交易支付结果
 *
 * @author xwbing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
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

    public static AliPayTradePayResult ofSuccess(AlipayTradePayResponse response) {
        return AliPayTradePayResult.builder().success(true).outTradeNo(response.getOutTradeNo())
                .tradeNo(response.getTradeNo()).buyerLogonId(response.getBuyerLogonId())
                .buyerUserId(response.getBuyerUserId()).totalAmount(response.getTotalAmount())
                .receiptAmount(response.getReceiptAmount()).gmtPayment(response.getGmtPayment())
                .fundBillList(response.getFundBillList()).build();
    }

    public static AliPayTradePayResult ofFail(AlipayTradePayResponse response) {
        return AliPayTradePayResult.builder().success(false).code(response.getSubCode()).message(response.getSubMsg())
                .build();
    }

    public static AliPayTradePayResult ofError() {
        return AliPayTradePayResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }

}