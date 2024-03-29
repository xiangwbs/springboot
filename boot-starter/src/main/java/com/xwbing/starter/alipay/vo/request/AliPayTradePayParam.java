package com.xwbing.starter.alipay.vo.request;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一收单交易支付参数
 *
 * @author xwbing
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTradePayParam {
    @ApiModelProperty("商户订单号")
    private String tradeNo;
    @ApiModelProperty("支付场景 条码支付:bar_code|声波支付:wave_code")
    private String scene;
    @ApiModelProperty("支付授权码")
    private String authCode;
    @ApiModelProperty("订单标题")
    private String subject;
    @ApiModelProperty("总金额 单位为元 精确到小数点后两位 [0.01,100000000]")
    private BigDecimal totalAmount;
    @ApiModelProperty("回调地址")
    private String notifyUrl;
    @ApiModelProperty("业务扩展参数")
    private AliPayExtendParam extendParam;

    public static AliPayTradePayParam bar(String tradeNo, String authCode, String subject, BigDecimal totalAmount,
            String notifyUrl) {
        //@formatter:off
        return AliPayTradePayParam
                .builder()
                .tradeNo(tradeNo)
                .authCode(authCode)
                .subject(subject)
                .totalAmount(totalAmount)
                .notifyUrl(notifyUrl)
                .scene("bar_code")
                .build();
    }

    public static AliPayTradePayParam wave(String tradeNo, String authCode, String subject, BigDecimal totalAmount,
            String notifyUrl) {
        //@formatter:off
        return AliPayTradePayParam
                .builder()
                .tradeNo(tradeNo)
                .authCode(authCode)
                .subject(subject)
                .totalAmount(totalAmount)
                .notifyUrl(notifyUrl)
                .scene("waveCode")
                .build();
    }

    public static String checkParam(AliPayTradePayParam param) {
        String message;
        if (StringUtils.isEmpty(param.getTradeNo())) {
            message = "商户订单号为空";
        } else if (StringUtils.isEmpty(param.getAuthCode())) {
            message = "授权码为空";
        } else if (StringUtils.isEmpty(param.getSubject())) {
            message = "订单标题为空";
        } else if (param.getTotalAmount().compareTo(BigDecimal.valueOf(0.01)) == -1) {
            message = "金额必须大于等于0.01元";
        } else if (StringUtils.isEmpty(param.getScene())) {
            message = "支付场景为空";
        } else {
            message = StringUtils.EMPTY;
        }
        return message;
    }
}
