package com.xwbing.service.service.pay.vo;

import java.util.Date;

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
     * 退款支付时间
     */
    private Date refundTime;

    public static AliPayTradeRefundResult ofSuccess(AlipayTradeRefundResponse response) {
        return AliPayTradeRefundResult.builder().success(true).message(response.getMsg()).code(response.getCode())
                .refundTime(response.getGmtRefundPay()).build();
    }

    public static AliPayTradeRefundResult ofFail(AlipayTradeRefundResponse response) {
        return AliPayTradeRefundResult.builder().success(false).code(response.getSubCode())
                .message(response.getSubMsg()).build();
    }

    public static AliPayTradeRefundResult ofError() {
        return AliPayTradeRefundResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }
}
