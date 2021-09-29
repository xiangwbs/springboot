package com.xwbing.starter.alipay.vo.request;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电脑网站支付参数
 *
 * @author xwbing
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AliPayPagePayParam {
    @ApiModelProperty("商户订单号")
    private String tradeNo;
    @ApiModelProperty("总金额 单位为元 精确到小数点后两位 [0.01,100000000]")
    private BigDecimal totalAmount;
    @ApiModelProperty("订单标题")
    private String subject;
    @ApiModelProperty("页面跳转同步通知页面地址")
    private String returnUrl;
    @ApiModelProperty("回调地址")
    private String notifyUrl;
    @ApiModelProperty("业务扩展参数")
    private AliPayExtendParam extendParam;

    public static AliPayPagePayParam of(String tradeNo, String subject, BigDecimal totalAmount, String returnUrl,
            String notifyUrl) {
        //@formatter:off
        return AliPayPagePayParam
                .builder()
                .tradeNo(tradeNo)
                .subject(subject).totalAmount(totalAmount)
                .returnUrl(returnUrl)
                .notifyUrl(notifyUrl)
                .build();
    }
}
