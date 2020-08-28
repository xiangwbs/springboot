package com.xwbing.starter.alipay.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 支付宝退款查询结果
 *
 * @author xwbing
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
    private BigDecimal totalAmount;
    /**
     * 本次退款请求，对应的退款金额
     */
    private BigDecimal refundAmount;
    /**
     * 如果有查询数据，且refund_status为空或为REFUND_SUCCESS，则代表退款成功
     */
    private String refundStatus;

    public static AliPayRefundQueryResult ofSuccess(AlipayTradeFastpayRefundQueryResponse response) {
        return AliPayRefundQueryResult.builder().success(true).code(response.getCode()).message(response.getMsg())
                .refundStatus(response.getRefundStatus()).totalAmount(new BigDecimal(response.getTotalAmount()))
                .refundAmount(new BigDecimal(response.getRefundAmount())).refundReason(response.getRefundReason())
                .refundTime(response.getGmtRefundPay()).build();
    }

    public static AliPayRefundQueryResult ofFail(AlipayTradeFastpayRefundQueryResponse response) {
        return AliPayRefundQueryResult.builder().success(false).code(response.getSubCode())
                .message(response.getSubMsg()).build();
    }

    public static AliPayRefundQueryResult ofError() {
        return AliPayRefundQueryResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }
}