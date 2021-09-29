package com.xwbing.starter.alipay.vo.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.lang3.StringUtils;

import com.alipay.api.response.AlipayTradeRefundResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 支付宝退款结果
 *
 * @author xwbing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayTradeRefundResult extends AliPayBaseResult {
    /**
     * 商户订单号
     */
    private String tradeNo;
    /**
     * 支付宝交易号
     */
    private String outTradeNo;
    /**
     * 用户的登录id
     */
    private String buyerLogonId;
    /**
     * 买家在支付宝的用户id
     */
    private String buyerUserId;
    /**
     * 退款总金额
     * 指该笔交易累计已经退款成功的金额
     */
    private BigDecimal refundFee;
    /**
     * 退款支付时间
     */
    private LocalDateTime refundTime;

    public static AliPayTradeRefundResult ofSuccess(AlipayTradeRefundResponse response) {
        //@formatter:off
        return AliPayTradeRefundResult
                .builder()
                .success(true)
                .message(response.getMsg())
                .code(response.getCode())
                .tradeNo(response.getOutTradeNo())
                .outTradeNo(response.getTradeNo())
                .buyerLogonId(response.getBuyerLogonId())
                .buyerUserId(response.getBuyerUserId())
                .refundFee(StringUtils.isNotEmpty(response.getRefundFee()) ? new BigDecimal(response.getRefundFee()) : null)
                .refundTime(response.getGmtRefundPay() != null ? LocalDateTime.ofInstant(response.getGmtRefundPay().toInstant(), ZoneId.systemDefault()) : null)
                .build();
    }

    public static AliPayTradeRefundResult ofFail(AlipayTradeRefundResponse response) {
        return AliPayTradeRefundResult.builder().success(false).code(response.getSubCode())
                .message(response.getSubMsg()).build();
    }

    public static AliPayTradeRefundResult ofError() {
        return AliPayTradeRefundResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }
}
