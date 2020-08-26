package com.xwbing.service.domain.mapper.sys;

import com.xwbing.service.domain.entity.sys.SysUserRole;
import com.xwbing.service.domain.mapper.BaseMapper;

import java.util.List;

/**
 * 创建时间: 2018/6/1 21:50
 * 作者: xiangwb
 * 说明:
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    List<SysUserRole> findByUserId(String userId);
}
