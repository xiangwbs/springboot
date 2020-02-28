package com.xwbing.domain.repository.sys;

import com.xwbing.domain.entity.sys.SysAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 说明: 权限数据访问层
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/5 16:44
 * 作者:  xiangwb
 */
public interface SysAuthorityRepository extends JpaRepository<SysAuthority, String> {
    SysAuthority getByCode(String code);

    List<SysAuthority> getByEnableOrderBySort(String enable);

    List<SysAuthority> getByParentIdAndEnableOrderBySort(String parentId, String enable);

    List<SysAuthority> getByParentIdOrderBySort(String parentId);

    List<SysAuthority> getByEnableAndIdInOrderBySort(String enable, List<String> ids);

    List<SysAuthority> getByIdInOrderBySort(List<String> ids);

    SysAuthority getBySort(int sort);
}
