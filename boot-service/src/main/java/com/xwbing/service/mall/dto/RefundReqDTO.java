package com.xwbing.service.mall.dto;

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
public class RefundReqDTO {
    /**
     * 退款号
     */
    private String refundNo;
    /**
     * 交易流水号
     */
    private String tradeNo;
    /**
     * 支付宝/微信交易流水号
     */
    private String outTradeNo;
    /**
     * 支付总金额
     */
    private Long totalAmount;
    /**
     * 退款金额 单位分
     */
    private Long refundAmount;
    /**
     * 退款的原因说明
     */
    private String refundReason;
}
