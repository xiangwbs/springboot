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
    @ApiModelProperty("商户订单号")
    private String tradeNo;
    @ApiModelProperty("订单标题")
    private String subject;
    @ApiModelProperty("总金额 单位为元 精确到小数点后两位 [0.01,100000000]")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "回调地址")
    private String notifyUrl;
    @ApiModelProperty("业务扩展参数")
    private AliPayExtendParam extendParam;

    public static AliPayTradePreCreateParam of(String tradeNo, String subject, BigDecimal totalAmount,
            String notifyUrl) {
        //@formatter:off
        return AliPayTradePreCreateParam
                .builder()
                .tradeNo(tradeNo)
                .subject(subject)
                .totalAmount(totalAmount)
                .notifyUrl(notifyUrl)
                .build();
    }
}