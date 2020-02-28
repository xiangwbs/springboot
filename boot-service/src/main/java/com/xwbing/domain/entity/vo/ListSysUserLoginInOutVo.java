package com.xwbing.domain.entity.vo;

import com.xwbing.domain.entity.sys.SysUserLoginInOut;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2018/1/18 16:05
 * 作者: xiangwb
 * 说明:
 */
@Data
@ApiModel
public class ListSysUserLoginInOutVo extends RestMessageVo {
    @ApiModelProperty(value = "返回数据")
    private List<SysUserLoginInOut> data;
}
