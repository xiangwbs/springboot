package com.xwbing.domain.entity.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

/**
 * 说明: 角色
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "sys_role")
@ApiModel
public class SysRole extends BaseEntity {
    private static final long serialVersionUID = -3048021197170624143L;
    public static String table = "sys_role";
    @ApiModelProperty(value = "角色名称", required = true)
    private String name;
    @ApiModelProperty(value = "角色编码", required = true)
    private String code;
    @ApiModelProperty(value = "是否启用(Y|N)", example = "Y", required = true)
    private String enable;
    @ApiModelProperty(value = "描述", required = true)
    private String remark;
}
