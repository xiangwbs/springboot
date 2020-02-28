package com.xwbing.domain.repository.sys;

import com.xwbing.domain.entity.sys.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 说明: 角色数据访问层
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/5 16:44
 * 作者:  xiangwb
 */
public interface SysRoleRepository extends JpaRepository<SysRole, String> {
    List<SysRole> getByEnable(String enable);

    SysRole getByCode(String code);

    List<SysRole> getByEnableAndIdIn(String enable, List<String> ids);

    List<SysRole> getByIdIn(List<String> ids);
}
