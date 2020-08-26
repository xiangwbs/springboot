package com.xwbing.service.domain.entity.vo;

import com.xwbing.service.domain.entity.sys.SysConfig;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 创建时间: 2018/1/18 15:44
 * 作者: xiangwb
 * 说明:
 */
@Data
@ApiModel
public class ListSysConfigVo extends RestMessageVo {
    @ApiModelProperty(value = "返回数据")
    private List<SysConfig> data;
}
