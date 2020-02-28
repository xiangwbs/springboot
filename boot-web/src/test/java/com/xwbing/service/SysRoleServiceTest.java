package com.xwbing.service;

import com.xwbing.BaseTest;
import com.xwbing.domain.entity.sys.SysRole;
import com.xwbing.domain.mapper.sys.SysRoleMapper;
import com.xwbing.service.sys.SysRoleService;
import com.xwbing.util.Pagination;
import com.xwbing.util.RestMessage;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;

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
        Assert.assertTrue(rest.getMessage(), rest.isSuccess());
        //getById
        sysRole = sysRoleService.getById(rest.getId());
        Assert.assertNotNull(sysRole);
        //update
        sysRole.setName("updateTest");
        rest = sysRoleService.update(sysRole);
        Assert.assertTrue(rest.getMessage(), rest.isSuccess());
        Assert.assertNotSame("修改失败", sysRole, sysRoleService.getById(rest.getId()));
        //listAllByEnable
        Assert.assertNotNull(sysRoleService.pageByEnable("Y", new Pagination<>()));
        //removeById
        Assert.assertTrue("删除失败", sysRoleService.removeById(rest.getId()).isSuccess());
        //............................
    }
}