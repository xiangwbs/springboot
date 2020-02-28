package com.xwbing.domain.entity.sys;

import com.xwbing.domain.entity.JpaBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 创建时间: 2018/2/26 10:26
 * 作者: xiangwb
 * 说明: 数据字典
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "data_dict")
public class DataDictionary extends JpaBaseEntity {
    private static final long serialVersionUID = -3409347240188002427L;
    @ApiModelProperty(value = "编码", required = true)
    private String code;
    @ApiModelProperty(value = "名称", required = true)
    private String name;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "父键")
    @Column(name = "parent_id")
    private String parentId;
    @ApiModelProperty(value = "是否启用(Y|N)", example = "Y", required = true)
    private String enable;
}
