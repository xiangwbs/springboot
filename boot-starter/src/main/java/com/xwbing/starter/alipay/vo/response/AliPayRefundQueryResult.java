package com.xwbing.starter.alipay.vo.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.lang3.StringUtils;

import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.xwbing.starter.alipay.enums.AliPayRefundStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 支付宝退款查询结果
 *
 * @author xwbing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayRefundQueryResult extends AliPayBaseResult {
    /**
     * 退款请求号
     */
    private String requestNo;
    /**
     * 商户流水号
     */
    private String tradeNo;
    /**
     * 第三方流水号
     */
    private String outTradeNo;
    /**
     * 退款原因
     */
    private String refundReason;
    /**
     * 退款时间
     */
    private LocalDateTime refundTime;
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
    private AliPayRefundStatusEnum refundStatus;

    public static AliPayRefundQueryResult ofSuccess(AlipayTradeFastpayRefundQueryResponse response) {
        //@formatter:off
        return AliPayRefundQueryResult
                .builder()
                .success(true)
                .code(response.getCode())
                .message(response.getMsg())
                .requestNo(response.getOutRequestNo())
                .tradeNo(response.getOutTradeNo())
                .outTradeNo(response.getTradeNo())
                .refundStatus(AliPayRefundStatusEnum.parse(response.getRefundStatus()))
                .totalAmount(StringUtils.isNotEmpty(response.getTotalAmount())?new BigDecimal(response.getTotalAmount()):null)
                .refundAmount(StringUtils.isNotEmpty(response.getRefundAmount())?new BigDecimal(response.getRefundAmount()):null)
                .refundReason(response.getRefundReason())
                .refundTime(response.getGmtRefundPay() != null ? LocalDateTime.ofInstant(response.getGmtRefundPay().toInstant(), ZoneId.systemDefault()) : null)
                .build();
    }

    public static AliPayRefundQueryResult ofFail(AlipayTradeFastpayRefundQueryResponse response) {
        return AliPayRefundQueryResult.builder().success(false).code(response.getSubCode())
                .message(response.getSubMsg()).build();
    }

    public static AliPayRefundQueryResult ofError() {
        return AliPayRefundQueryResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }
}