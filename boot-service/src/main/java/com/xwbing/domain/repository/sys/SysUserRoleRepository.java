package com.xwbing.domain.repository.sys;

import com.xwbing.domain.entity.sys.SysUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2017/11/14 14:13
 * 作者: xiangwb
 * 说明: 用户角色数据访问层
 */
public interface SysUserRoleRepository extends JpaRepository<SysUserRole, String> {
    List<SysUserRole> getByUserId(String userId);
}
