package com.xwbing.domain.entity.sys;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * 说明: 用户角色关系
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "sys_user_role")
public class SysUserRole extends BaseEntity {
    private static final long serialVersionUID = 7340808603856635810L;
    public static String table = "sys_user_role";
    /**
     * 用户主键
     */
    @Column(name = "user_id")
    private String userId;
    /**
     * 角色主键
     */
    @Column(name = "role_id")
    private String roleId;
}
