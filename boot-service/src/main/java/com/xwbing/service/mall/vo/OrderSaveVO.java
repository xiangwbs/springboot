package com.xwbing.service.mall.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年09月30日 1:40 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderSaveVO {
    @ApiModelProperty(value = "是否需要支付")
    private boolean needPay;
    @ApiModelProperty(value = "订单号")
    private String orderNo;
}
