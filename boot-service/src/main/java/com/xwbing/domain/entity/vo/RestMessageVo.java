package com.xwbing.domain.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2018/1/18 15:25
 * 作者: xiangwb
 * 说明:
 */
@Data
@ApiModel
public class RestMessageVo {
    @ApiModelProperty(value = "是否成功")
    private boolean success;
    @ApiModelProperty(value = "返回消息")
    private String message;
    @ApiModelProperty(value = "新增、修改主鍵返回id")
    private String id;
}
