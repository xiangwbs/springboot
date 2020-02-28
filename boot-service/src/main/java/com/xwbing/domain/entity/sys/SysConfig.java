package com.xwbing.domain.entity.sys;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

/**
 * 说明: 系统配置
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "system_config")
public class SysConfig extends BaseEntity {
    private static final long serialVersionUID = -7587016038432881980L;
    public static String table = "system_config";
    /**
     * 配置项的code
     */
    private String code;
    /**
     * 配置项的值
     */
    private String value;
    /**
     * 配置项的描述(名称)
     */
    private String name;
    /**
     * 是否启用(Y|N)
     */
    private String enable;
}
