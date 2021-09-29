package com.xwbing.service.pay.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.xwbing.service.pay.enums.TradeStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付通知回调信息
 *
 * @author daofeng
 * @version $
 * @since 2020/10/28 00:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayNotifyDTO<T> implements Serializable {
    /**
     * 交易状态
     */
    private TradeStatusEnum tradeStatus;
    /**
     * 验签是否通过
     */
    private Boolean signValid;
    /**
     * 商户订单号（内部交易流水号）
     */
    private String tradeNo;
    /**
     * 支付宝交易流水号/微信支付流水号
     */
    private String outTradeNo;
    /**
     * 订单金额 单位分
     */
    private Long totalAmount;
    /**
     * 交易付款时间
     */
    private LocalDateTime paidTime;
    /**
     * 支付回调信息
     */
    private T notifyInfo;
}
