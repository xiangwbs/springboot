package com.xwbing.domain.entity.vo;

import com.xwbing.domain.entity.sys.SysUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创建时间: 2018/1/18 16:00
 * 作者: xiangwb
 * 说明:
 */
@Data
@ApiModel
public class SysUserVo extends RestMessageVo {
    @ApiModelProperty(value = " 返回数据")
    private SysUser data;
}
