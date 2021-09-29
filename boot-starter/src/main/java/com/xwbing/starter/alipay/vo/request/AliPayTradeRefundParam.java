package com.xwbing.starter.alipay.vo.request;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 支付宝退款参数
 *
 * @author xwbing
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayTradeRefundParam {
    @ApiModelProperty("商户订单号  订单号和支付宝交易号2选1")
    private String tradeNo;
    @ApiModelProperty("支付宝交易号(推荐) 订单号和支付宝交易号2选1")
    private String outTradeNo;
    @ApiModelProperty("退款请求号(部分退款，此参数必传)")
    private String requestNo;
    @ApiModelProperty("退款金额")
    private BigDecimal refundAmount;
    @ApiModelProperty("退款原因")
    private String refundReason;

    public static AliPayTradeRefundParam of(String requestNo, String tradeNo, String outTradeNo,
            BigDecimal refundAmount, String refundReason) {
        //@formatter:off
        return AliPayTradeRefundParam
                .builder()
                .requestNo(requestNo)
                .outTradeNo(outTradeNo)
                .tradeNo(tradeNo)
                .refundAmount(refundAmount)
                .refundReason(refundReason)
                .build();
    }
}
