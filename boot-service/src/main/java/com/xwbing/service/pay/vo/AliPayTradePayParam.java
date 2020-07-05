package com.xwbing.service.pay.vo;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.annotation.JSONField;

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
    @JSONField(name = "out_trade_no")
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
    @JSONField(name = "auth_code")
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
    @JSONField(name = "total_amount")
    private BigDecimal totalAmount;
    /**
     * 支付超时时间
     * 1m～15d。m-分钟，h-小时，d-天
     */
    @JSONField(name = "timeout_express")
    private String timeoutExpress;
    /**
     * hb_fq_num 花呗分期数 仅支持传入 3、6、12
     * hb_fq_seller_percent 卖家承担收费比例 商家承担手续费传入 100，用户承担手续费传入 0，仅支持传入 100、0 两种
     */
    @JSONField(name = "extend_params")
    private AliPayExtendParam extendParams;

    public static AliPayTradePayParam barCode(String outTradeNo, String authCode, String subject,
            BigDecimal totalAmount, AliPayExtendParam extendParams) {
        return AliPayTradePayParam.builder().outTradeNo(outTradeNo).authCode(authCode).subject(subject)
                .totalAmount(totalAmount).extendParams(extendParams).scene("bar_code").timeoutExpress("10m").build();
    }

    public static AliPayTradePayParam waveCode(String outTradeNo, String authCode, String subject,
            BigDecimal totalAmount, AliPayExtendParam extendParams) {
        return AliPayTradePayParam.builder().outTradeNo(outTradeNo).authCode(authCode).subject(subject)
                .totalAmount(totalAmount).extendParams(extendParams).scene("waveCode").timeoutExpress("10m").build();
    }

    public static String checkParam(AliPayTradePayParam param) {
        String message;
        if (StringUtils.isEmpty(param.getOutTradeNo())) {
            message = "商户订单号为空";
        } else if (StringUtils.isEmpty(param.getAuthCode())) {
            message = "授权码为空";
        } else if (StringUtils.isEmpty(param.getSubject())) {
            message = "订单标题为空";
        } else if (param.getTotalAmount().compareTo(BigDecimal.ZERO) < 1) {
            message = "金额必须大于0";
        } else if (StringUtils.isEmpty(param.getScene())) {
            message = "支付场景为空";
        } else {
            message = StringUtils.EMPTY;
        }
        return message;
    }
}
