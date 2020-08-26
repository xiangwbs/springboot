package com.xwbing.web.controller.sys;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.config.aspect.annotation.Idempotent;
import com.xwbing.service.domain.entity.sys.SysAuthority;
import com.xwbing.service.domain.entity.sys.SysRole;
import com.xwbing.service.domain.entity.sys.SysRoleAuthority;
import com.xwbing.service.domain.entity.vo.PageSysRoleVo;
import com.xwbing.service.domain.entity.vo.RestMessageVo;
import com.xwbing.service.domain.entity.vo.SysRoleVo;
import com.xwbing.service.service.sys.SysAuthorityService;
import com.xwbing.service.service.sys.SysRoleAuthorityService;
import com.xwbing.service.service.sys.SysRoleService;
import com.xwbing.service.util.JsonResult;
import com.xwbing.service.util.Pagination;
import com.xwbing.service.util.RestMessage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2017/11/14 10:41
 * 作者: xiangwb
 * 说明: 角色控制层
 */
@Api(tags = "sysRoleApi", description = "角色相关接口")
@RestController
@RequestMapping("/role/")
public class SysRoleControl {
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysAuthorityService sysAuthorityService;
    @Resource
    private SysRoleAuthorityService sysRoleAuthorityService;

    @Idempotent
    @ApiOperation(value = "添加角色", response = RestMessageVo.class)
    @ApiImplicitParam(name = "sign", value = "签名", paramType = "header", dataType = "string")
    @PostMapping("save")
    public JSONObject save(@RequestBody SysRole sysRole) {
        RestMessage result = sysRoleService.save(sysRole);
        return JsonResult.toJSONObj(result);
    }

    @ApiOperation(value = "删除角色", response = RestMessageVo.class)
    @DeleteMapping("removeById/{id}")
    public JSONObject removeById(@PathVariable String id) {
        if (StringUtils.isEmpty(id)) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        RestMessage result = sysRoleService.removeById(id);
        return JsonResult.toJSONObj(result);
    }

    @ApiOperation(value = "修改角色", response = RestMessageVo.class)
    @PutMapping("update")
    public JSONObject update(@RequestBody SysRole sysRole) {
        if (StringUtils.isEmpty(sysRole.getId())) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        RestMessage result = sysRoleService.update(sysRole);
        return JsonResult.toJSONObj(result);
    }

    @ApiOperation(value = "获取角色详情", response = SysRoleVo.class)
    @GetMapping("getById")
    public JSONObject getById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        SysRole sysRole = sysRoleService.getById(id);
        if (sysRole == null) {
            return JsonResult.toJSONObj("该角色不存在");
        }
        return JsonResult.toJSONObj(sysRole, "");
    }

    @ApiOperation(value = "根据是否启用分页查询所有角色", response = PageSysRoleVo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "1", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示的条数", defaultValue = "10", paramType = "query", dataType = "int")
    })
    @GetMapping("pageByEnable")
    public JSONObject pageByEnable(@RequestParam(required = false) String enable, @ApiIgnore Pagination page) {
        Pagination pagination = sysRoleService.pageByEnable(enable, page);
        return JsonResult.toJSONObj(pagination, "");
    }

    @ApiOperation(value = "根据角色主键查找权限", response = PageSysRoleVo.class)
    @GetMapping("listAuthorityByRoleId")
    public JSONObject listAuthorityByRoleId(@RequestParam String roleId, @RequestParam(required = false) String enable) {
        if (StringUtils.isEmpty(roleId)) {
            return JsonResult.toJSONObj("角色主键不能为空");
        }
        SysRole sysRole = sysRoleService.getById(roleId);
        if (sysRole == null) {
            return JsonResult.toJSONObj("该角色不存在");
        }
        List<SysAuthority> authoritys = sysAuthorityService.listByRoleIdEnable(roleId, enable);
        return JsonResult.toJSONObj(authoritys, "");
    }

    @ApiOperation(value = "保存角色权限", response = RestMessageVo.class)
    @PostMapping("saveAuthority")
    public JSONObject saveAuthority(@RequestParam String authorityIds, @RequestParam String roleId) {
        if (StringUtils.isEmpty(authorityIds)) {
            return JsonResult.toJSONObj("权限主键集合不能为空");
        }
        if (StringUtils.isEmpty(roleId)) {
            return JsonResult.toJSONObj("角色主键不能为空");
        }
        SysRole sysRole = sysRoleService.getById(roleId);
        if (sysRole == null) {
            return JsonResult.toJSONObj("该角色不存在");
        }
        String[] ids = authorityIds.split(",");
        List<SysRoleAuthority> list = new ArrayList<>();
        SysRoleAuthority roleAuthority;
        for (String id : ids) {
            roleAuthority = new SysRoleAuthority();
            roleAuthority.setRoleId(roleId);
            roleAuthority.setAuthorityId(id);
            list.add(roleAuthority);
        }
        RestMessage restMessage = sysRoleAuthorityService.saveBatch(list, roleId);
        return JsonResult.toJSONObj(restMessage);
    }
}
