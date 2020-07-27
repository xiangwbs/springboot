package com.xwbing.service.pay.vo;

import com.alipay.api.response.AlipayTradeCloseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 统一收单线下交易关闭结果
 *
 * @author xwbing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayTradeCloseResult extends AliPayBaseResult {
    private String tradeNo;
    private String outTradeNo;

    public static AliPayTradeCloseResult ofSuccess(AlipayTradeCloseResponse response) {
        return AliPayTradeCloseResult.builder().success(true).tradeNo(response.getTradeNo())
                .outTradeNo(response.getOutTradeNo()).message(response.getMsg()).code(response.getCode()).build();
    }

    public static AliPayTradeCloseResult ofFail(AlipayTradeCloseResponse response) {
        return AliPayTradeCloseResult.builder().success(false).message(response.getSubMsg()).code(response.getSubCode())
                .build();
    }

    public static AliPayTradeCloseResult ofError() {
        return AliPayTradeCloseResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }
}
