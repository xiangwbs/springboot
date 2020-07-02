package com.xwbing.service.pay.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 说明: 支付宝退款查询结果
 * 创建时间: 2017/5/10 17:38
 * 作者:  xiangwb
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayRefundQueryResult extends AliPayBaseResult {
    /**
     * 退款原因
     */
    private String refundReason;
    /**
     * 退款时间
     */
    private Date refundTime;
    /**
     * 该笔退款所对应的交易的订单金额
     */
    private String totalAmount;
    /**
     * 本次退款请求，对应的退款金额
     */
    private String refundAmount;
    /**
     * 为空或为REFUND_SUCCESS，则代表退款成功
     */
    private String refundStatus;
}