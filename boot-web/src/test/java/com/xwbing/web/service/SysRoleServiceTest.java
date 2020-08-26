package com.xwbing.web.service;

import javax.annotation.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.xwbing.web.BaseTest;
import com.xwbing.service.domain.entity.sys.SysRole;
import com.xwbing.service.service.sys.SysRoleService;
import com.xwbing.service.util.Pagination;
import com.xwbing.service.util.RestMessage;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2018/6/1 13:20
 * 作者: xiangwb
 * 说明: 权限服务层测试
 */
public class SysRoleServiceTest extends BaseTest {
    @Resource
    private SysRoleService sysRoleService;

    @Test
    public void all() {
        //save
        SysRole sysRole = new SysRole();
        sysRole.setCode("serviceTest");
        sysRole.setEnable("Y");
        sysRole.setName("serviceTest");
        sysRole.setRemark("serviceTest");
        RestMessage rest = sysRoleService.save(sysRole);
        Assertions.assertTrue(rest.isSuccess(), rest.getMessage());
        //getById
        sysRole = sysRoleService.getById(rest.getId());
        Assertions.assertNotNull(sysRole);
        //update
        sysRole.setName("updateTest");
        rest = sysRoleService.update(sysRole);
        Assertions.assertTrue(rest.isSuccess(), rest.getMessage());
        Assertions.assertNotSame(sysRole, sysRoleService.getById(rest.getId()), "修改失败");
        //listAllByEnable
        Assertions.assertNotNull(sysRoleService.pageByEnable("Y", new Pagination<>()));
        //removeById
        Assertions.assertTrue(sysRoleService.removeById(rest.getId()).isSuccess(), "删除失败");
        //............................
    }
}