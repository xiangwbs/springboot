package com.xwbing.domain.entity.sys;

import com.xwbing.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 说明: 用户角色关系
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserRole extends BaseEntity {
    private static final long serialVersionUID = 7340808603856635810L;
    public static String table = "sys_user_role";
    /**
     * 用户主键
     */
    private String userId;
    /**
     * 角色主键
     */
    private String roleId;
}
