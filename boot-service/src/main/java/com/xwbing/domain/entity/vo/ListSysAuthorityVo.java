package com.xwbing.domain.entity.vo;

import com.xwbing.domain.entity.sys.SysAuthority;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2018/1/18 15:52
 * 作者: xiangwb
 * 说明:
 */
@Data
@ApiModel
public class ListSysAuthorityVo extends RestMessageVo {
    @ApiModelProperty(value = "返回数据")
    private List<SysAuthority> data;
}
