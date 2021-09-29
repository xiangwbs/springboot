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
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AliPayWapPayParam {
    @ApiModelProperty("订单标题")
    private String subject;
    @ApiModelProperty("商户订单号")
    private String tradeNo;
    @ApiModelProperty("总金额 单位为元 精确到小数点后两位 [0.01,100000000]")
    private BigDecimal totalAmount;
    @ApiModelProperty("用户付款中途退出返回商户网站的地址")
    private String quitUrl;
    @ApiModelProperty("页面跳转同步通知页面地址")
    private String returnUrl;
    @ApiModelProperty("回调地址")
    private String notifyUrl;
    @ApiModelProperty("业务扩展参数")
    private AliPayExtendParam extendParam;

    public static AliPayWapPayParam of(String tradeNo, String subject, BigDecimal totalAmount, String returnUrl,
            String quitUrl, String notifyUrl) {
        //@formatter:off
        return AliPayWapPayParam
                .builder()
                .tradeNo(tradeNo)
                .subject(subject)
                .totalAmount(totalAmount)
                .returnUrl(returnUrl)
                .quitUrl(quitUrl)
                .notifyUrl(notifyUrl)
                .build();
    }
}
