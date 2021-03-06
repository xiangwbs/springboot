package com.xwbing.starter.alipay.vo.request;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交易预下单参数
 *
 * @author xwbing
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTradePreCreateParam {
    @ApiModelProperty(value = "商户订单号")
    private String outTradeNo;
    @ApiModelProperty(value = "订单标题")
    private String subject;
    @ApiModelProperty(value = "总金额 单位为元 精确到小数点后两位 [0.01,100000000]")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "回调地址")
    private String notifyUrl;
    private AliPayExtendParam extendParam;

    public static AliPayTradePreCreateParam build(String outTradeNo, String subject, BigDecimal totalAmount,
            String notifyUrl) {
        return AliPayTradePreCreateParam.builder().outTradeNo(outTradeNo).subject(subject).totalAmount(totalAmount)
                .notifyUrl(notifyUrl).build();
    }
}