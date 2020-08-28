package com.xwbing.starter.alipay.vo.response;

import com.alipay.api.response.AlipayTradePrecreateResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 交易预下单结果
 *
 * @author xwbing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayTradePreCreateResult extends AliPayBaseResult {
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 二维码
     */
    private String qrCode;

    public static AliPayTradePreCreateResult ofSuccess(AlipayTradePrecreateResponse response) {
        return AliPayTradePreCreateResult.builder().success(true).code(response.getCode()).message(response.getMsg())
                .outTradeNo(response.getOutTradeNo()).qrCode(response.getQrCode()).build();
    }

    public static AliPayTradePreCreateResult ofFail(AlipayTradePrecreateResponse response) {
        return AliPayTradePreCreateResult.builder().success(false).code(response.getSubCode())
                .message(response.getSubMsg()).build();
    }

    public static AliPayTradePreCreateResult ofError() {
        return AliPayTradePreCreateResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }
}