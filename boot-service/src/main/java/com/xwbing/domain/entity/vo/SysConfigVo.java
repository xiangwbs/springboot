package com.xwbing.domain.entity.vo;

import com.xwbing.domain.entity.sys.SysConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创建时间: 2018/1/18 15:28
 * 作者: xiangwb
 * 说明:
 */
@Data
@ApiModel
public class SysConfigVo extends RestMessageVo {
    @ApiModelProperty(value = "返回数据")
    private SysConfig data;
}
