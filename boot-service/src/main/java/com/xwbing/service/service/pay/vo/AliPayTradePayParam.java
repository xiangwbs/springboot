package com.xwbing.service.service.pay.vo;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTradePayParam {
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 支付场景
     * 条码支付:bar_code
     * 声波支付:wave_code
     */
    private String scene;
    /**
     * 支付授权码
     */
    private String authCode;
    /**
     * 订单标题
     */
    private String subject;
    /**
     * 总金额
     * 单位为元，精确到小数点后两位
     * 取值范围[0.01,100000000]
     */
    private BigDecimal totalAmount;
    private AliPayExtendParam extendParam;

    public static AliPayTradePayParam bar(String outTradeNo, String authCode, String subject, BigDecimal totalAmount) {
        return AliPayTradePayParam.builder().outTradeNo(outTradeNo).authCode(authCode).subject(subject)
                .totalAmount(totalAmount).scene("bar_code").build();
    }

    public static AliPayTradePayParam wave(String outTradeNo, String authCode, String subject, BigDecimal totalAmount) {
        return AliPayTradePayParam.builder().outTradeNo(outTradeNo).authCode(authCode).subject(subject)
                .totalAmount(totalAmount).scene("waveCode").build();
    }

    public static String checkParam(AliPayTradePayParam param) {
        String message;
        if (StringUtils.isEmpty(param.getOutTradeNo())) {
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