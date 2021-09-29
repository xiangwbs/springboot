package com.xwbing.service.pay.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.xwbing.service.util.DecimalUtil;
import com.xwbing.starter.alipay.vo.response.AliPayRefundQueryResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 交易退款请求
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年07月24日 10:18
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class RefundQueryRespDTO {
    /**
     * 退款请求号
     */
    private String refundNo;
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
    private Long totalAmount;
    /**
     * 本次退款请求，对应的退款金额
     */
    private BigDecimal refundAmount;

    public static RefundQueryRespDTO of(AliPayRefundQueryResult result) {
        return RefundQueryRespDTO.builder().refundNo(result.getRequestNo()).tradeNo(result.getTradeNo())
                .outTradeNo(result.getOutTradeNo()).refundReason(result.getRefundReason())
                .refundTime(result.getRefundTime()).totalAmount(DecimalUtil.toFen(result.getTotalAmount()))
                .refundAmount(result.getRefundAmount()).build();
    }
}
