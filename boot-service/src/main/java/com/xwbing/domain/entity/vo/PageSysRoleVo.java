package com.xwbing.domain.entity.vo;

import com.xwbing.domain.entity.sys.SysRole;
import com.xwbing.util.Pagination;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创建时间: 2018/1/18 15:57
 * 作者: xiangwb
 * 说明:
 */
@Data
@ApiModel
public class PageSysRoleVo extends RestMessageVo {
    @ApiModelProperty(value = "返回数据")
    private Pagination<SysRole> data;
}
