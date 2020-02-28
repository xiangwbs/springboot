package com.xwbing.domain.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * 说明: 基础类
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Data
@ApiModel
@MappedSuperclass
public class JpaBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @GeneratedValue(generator = "idGenerator")
    @Column(length = 50)
    @ApiModelProperty(value = "主键")
    private String id;
    @ApiModelProperty(value = "创建者", hidden = true)
    private String creator;
    @ApiModelProperty(value = "修改者", hidden = true)
    private String modifier;
    @Column(name = "create_time")
    @ApiModelProperty(value = "创建时间", hidden = true)
    private Date createTime;
    @Column(name = "modified_time")
    @ApiModelProperty(value = "修改时间", hidden = true)
    private Date modifiedTime;
}
