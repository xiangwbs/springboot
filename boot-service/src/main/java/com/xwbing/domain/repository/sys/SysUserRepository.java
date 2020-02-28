package com.xwbing.domain.repository.sys;

import com.xwbing.domain.entity.sys.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 说明: 用户数据访问层
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/5 16:44
 * 作者:  xiangwb
 */
public interface SysUserRepository extends JpaRepository<SysUser, String> {
    SysUser getByUserName(String name);
}
