package com.xwbing.starter.alipay.vo;

import com.alipay.api.response.AlipayTradeCreateResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 统一收单交易创建结果
 *
 * @author xwbing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayTradeCreateResult extends AliPayBaseResult {
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 支付宝交易号
     */
    private String tradeNo;

    public static AliPayTradeCreateResult ofSuccess(AlipayTradeCreateResponse response) {
        return AliPayTradeCreateResult.builder().success(true).message(response.getMsg()).code(response.getCode())
                .outTradeNo(response.getOutTradeNo()).tradeNo(response.getTradeNo()).build();
    }

    public static AliPayTradeCreateResult ofFail(AlipayTradeCreateResponse response) {
        return AliPayTradeCreateResult.builder().success(false).code(response.getSubCode())
                .message(response.getSubMsg()).build();
    }

    public static AliPayTradeCreateResult ofError() {
        return AliPayTradeCreateResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }
}
