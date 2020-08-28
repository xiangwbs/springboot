package com.xwbing.starter.alipay.vo.request;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付宝转账参数
 *
 * @author xwbing
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTransferParam {
    @ApiModelProperty(value = "支持邮箱和手机号格式")
    private String payAccount;
    @ApiModelProperty(value = "参与方真实姓名")
    private String name;
    @ApiModelProperty(value = "商户订单号")
    private String outBizNo;
    @ApiModelProperty(value = "订单总金额，单位为元，精确到小数点后两位 [0.1,100000000]")
    private BigDecimal amount;
    @ApiModelProperty(value = "转账业务的标题")
    private String title;
}
