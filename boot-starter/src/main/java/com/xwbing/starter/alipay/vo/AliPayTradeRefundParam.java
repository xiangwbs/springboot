package com.xwbing.starter.alipay.vo;

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
    @ApiModelProperty(value = "商户订单号  订单号和支付宝交易号2选1")
    private String outTradeNo;
    @ApiModelProperty(value = "支付宝交易号(推荐) 订单号和支付宝交易号2选1")
    private String tradeNo;
    @ApiModelProperty(value = "退款请求号(部分退款，此参数必传)")
    private String outRequestNo;
    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmount;
    @ApiModelProperty(value = "退款原因")
    private String refundReason;

    public static AliPayTradeRefundParam build(String outRequestNo, String outTradeNo, BigDecimal refundAmount,
            String refundReason) {
        return AliPayTradeRefundParam.builder().outRequestNo(outRequestNo).outTradeNo(outTradeNo)
                .refundAmount(refundAmount).refundReason(refundReason).build();

    }

    public static AliPayTradeRefundParam build(String outRequestNo, String tradeNo, String refundReason,
            BigDecimal refundAmount) {
        return AliPayTradeRefundParam.builder().outRequestNo(outRequestNo).tradeNo(tradeNo).refundAmount(refundAmount)
                .refundReason(refundReason).build();
    }
}
