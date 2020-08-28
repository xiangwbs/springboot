package com.xwbing.starter.alipay.vo.request;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 手机网站支付参数
 *
 * @author xwbing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayWapPayParam {
    @ApiModelProperty(value = "订单标题")
    private String subject;
    @ApiModelProperty(value = "商户订单号")
    private String outTradeNo;
    @ApiModelProperty(value = "总金额 单位为元 精确到小数点后两位 [0.01,100000000]")
    private BigDecimal totalAmount;
    @ApiModelProperty(value = "用户付款中途退出返回商户网站的地址")
    private String quitUrl;
    @ApiModelProperty(value = "页面跳转同步通知页面地址")
    private String returnUrl;
    @ApiModelProperty(value = "回调地址")
    private String notifyUrl;
    private AliPayExtendParam extendParam;

    public static AliPayWapPayParam build(String outTradeNo, String subject, BigDecimal totalAmount, String returnUrl,
            String quitUrl, String notifyUrl) {
        return AliPayWapPayParam.builder().outTradeNo(outTradeNo).subject(subject).totalAmount(totalAmount)
                .returnUrl(returnUrl).quitUrl(quitUrl).notifyUrl(notifyUrl).build();
    }
}
