package com.xwbing.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回消息封装类
 *
 * @author xiangwb
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
    private Object data;
    @ApiModelProperty(value = "新增、修改主鍵返回id")
    private String id;
}
