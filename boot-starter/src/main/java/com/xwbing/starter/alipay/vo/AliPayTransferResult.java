package com.xwbing.starter.alipay.vo;

import com.alipay.api.response.AlipayFundTransUniTransferResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 转账结果
 *
 * @author xwbing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayTransferResult extends AliPayBaseResult {
    /**
     * 支付宝转账订单号
     */
    private String orderId;
    /**
     * 商户订单号
     */
    private String outBizNo;
    /**
     * 转账单据状态
     */
    private String status;
    /**
     * 订单支付时间，格式为yyyy-MM-dd HH:mm:ss
     */
    private String transDate;

    public static AliPayTransferResult ofSuccess(AlipayFundTransUniTransferResponse response) {
        return AliPayTransferResult.builder().success(true).code(response.getCode()).message(response.getMsg())
                .orderId(response.getOrderId()).outBizNo(response.getOutBizNo()).status(response.getStatus())
                .transDate(response.getTransDate()).build();
    }

    public static AliPayTransferResult ofFail(AlipayFundTransUniTransferResponse response) {
        return AliPayTransferResult.builder().success(false).code(response.getSubCode()).message(response.getSubMsg())
                .build();
    }

    public static AliPayTransferResult ofError() {
        return AliPayTransferResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }

}