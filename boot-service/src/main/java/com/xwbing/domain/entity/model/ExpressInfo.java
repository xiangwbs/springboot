package com.xwbing.domain.entity.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创建时间: 2017/11/17 9:26
 * 作者: xiangwb
 * 说明: 快递信息
 */
@Data
@ApiModel
public class ExpressInfo {
    @ApiModelProperty(value = "快递公司编号", example = "HTKY")
    private String shipperCode;
    @ApiModelProperty(value = "物流单号", example = "211386517825")
    private String logisticCode;
//    @ApiModelProperty(value = "快递名称", example = "百世汇通")
//    private String expressName;
//    @ApiModelProperty(value = "订单编号", example = "201703140000000018")
//    private String orderNo;
}
