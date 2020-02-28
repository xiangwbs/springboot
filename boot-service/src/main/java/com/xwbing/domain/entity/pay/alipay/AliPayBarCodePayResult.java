package com.xwbing.domain.entity.pay.alipay;

import com.alipay.api.domain.TradeFundBill;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * 说明: 条形码支付结果
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:36
 * 作者:  xiangwb
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AliPayBarCodePayResult extends AliPayBaseResult {
    public static final String HASPAYED = "ACQ.TRADE_HAS_SUCCESS";

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
    /**
     * 本次交易支付所使用的单品券优惠的商品优惠信息
     */
    private String discountGoodsDetail;

    public AliPayBarCodePayResult(boolean isSuccess) {
        this.setSuccess(isSuccess);
    }

    @Override
    public boolean isSuccess() {
        //如果已支付过也认为是成功
        return "10000".equals(super.getCode());
    }
}