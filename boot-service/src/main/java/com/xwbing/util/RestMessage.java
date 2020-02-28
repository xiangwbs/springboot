package com.xwbing.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 说明:返回消息封装类
 * 作者: xiangwb
 */
@Data
@ApiModel
public class RestMessage implements Serializable {
    private static final long serialVersionUID = -4167591341943919542L;
    @ApiModelProperty(value = "是否成功")
    private boolean success;
    @ApiModelProperty(value = "成功、错误返回提示信息")
    private String message;
    @ApiModelProperty(value = "返回的数据")
    private Object data;// 返回的数据
    @ApiModelProperty(value = "新增、修改主鍵返回id")
    private String id;
}
