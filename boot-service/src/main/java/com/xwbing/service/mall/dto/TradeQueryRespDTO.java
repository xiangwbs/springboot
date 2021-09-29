package com.xwbing.service.mall.dto;

import java.time.LocalDateTime;

import com.xwbing.service.mall.enums.TradeStatusEnum;
import com.xwbing.service.util.DecimalUtil;
import com.xwbing.starter.alipay.vo.response.AliPayTradeQueryResult;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年07月24日 10:18
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class TradeQueryRespDTO {
    /**
     * 商家订单号
     */
    private String tradeNo;
    /**
     * 三方订单号
     */
    private String outTradeNo;
    /**
     * 交易状态
     */
    private TradeStatusEnum tradeStatus;
    /**
     * 交易的订单金额，单位为分
     */
    private Long totalAmount;
    /**
     * 支付时间
     */
    private LocalDateTime paidTime;

    public static TradeQueryRespDTO of(AliPayTradeQueryResult result) {
        //@formatter:off
        return TradeQueryRespDTO
                .builder()
                .tradeNo(result.getOutTradeNo())
                .outTradeNo(result.getTradeNo())
                .totalAmount(DecimalUtil.toFen(result.getTotalAmount()))
                .paidTime(result.getPaidTime())
                .tradeStatus(TradeStatusEnum.of(result.getTradeStatus()))
                .build();
    }
}
