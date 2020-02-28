package com.xwbing.domain.entity.sys;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * 说明: 基础类
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Data
@MappedSuperclass
@ApiModel
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 8901948362657956187L;
    @Id
    @Column(length = 50)
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "创建者", hidden = true)
    private String creator;
    @ApiModelProperty(value = "修改者", hidden = true)
    private String modifier;
    @ApiModelProperty(value = "创建时间", hidden = true)
    @Column(name = "create_time")
    private Date createTime;
    @ApiModelProperty(value = "修改时间", hidden = true)
    @Column(name = "modified_time")
    private Date modifiedTime;
}
