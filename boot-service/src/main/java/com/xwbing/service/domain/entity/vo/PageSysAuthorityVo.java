package com.xwbing.service.domain.entity.vo;

import com.xwbing.service.domain.entity.sys.SysAuthority;
import com.xwbing.service.util.Pagination;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 创建时间: 2018/1/18 15:52
 * 作者: xiangwb
 * 说明:
 */
@Data
@ApiModel
public class PageSysAuthorityVo extends RestMessageVo {
    @ApiModelProperty(value = "返回数据")
    private Pagination<SysAuthority> data;
}
