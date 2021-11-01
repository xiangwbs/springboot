package com.xwbing.service.domain.entity.model;

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
}
