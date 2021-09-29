package com.xwbing.service.pay.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.xwbing.service.pay.enums.PayTypeEnum;
import com.xwbing.service.pay.enums.PayWayEnum;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PayOrderReqDTO {
    @NotNull(message = "支付类型不能为空")
    @ApiModelProperty("支付类型:10=微信,20=支付宝")
    private PayTypeEnum payType;
    @NotNull(message = "支付方式不能为空")
    @ApiModelProperty("支付方式")
    private PayWayEnum payWay;
    @NotEmpty(message = "订单号不能为空")
    @ApiModelProperty("订单号")
    private String orderNo;
}
