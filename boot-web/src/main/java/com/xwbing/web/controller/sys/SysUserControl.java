package com.xwbing.web.controller.sys;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
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
import com.xwbing.service.domain.entity.sys.SysAuthority;
import com.xwbing.service.domain.entity.sys.SysRole;
import com.xwbing.service.domain.entity.sys.SysUser;
import com.xwbing.service.domain.entity.sys.SysUserRole;
import com.xwbing.service.domain.entity.vo.ListSysAuthorityVo;
import com.xwbing.service.domain.entity.vo.ListSysRoleVo;
import com.xwbing.service.domain.entity.vo.PageSysUserVo;
import com.xwbing.service.domain.entity.vo.RestMessageVo;
import com.xwbing.service.domain.entity.vo.SysUserVo;
import com.xwbing.service.enums.MenuOrButtonEnum;
import com.xwbing.service.enums.YesOrNoEnum;
import com.xwbing.service.service.sys.SysAuthorityService;
import com.xwbing.service.service.sys.SysRoleService;
import com.xwbing.service.service.sys.SysUserRoleService;
import com.xwbing.service.service.sys.SysUserService;
import com.xwbing.service.util.JsonResult;
import com.xwbing.service.util.Pagination;
import com.xwbing.service.util.RestMessage;
import com.xwbing.service.util.ThreadLocalUtil;
import com.xwbing.starter.aspect.annotation.Lock;
import com.xwbing.starter.aspect.annotation.ReqIdempotent;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 说明: 用户控制层
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Api(tags = "userApi", description = "用户相关接口")
@RestController
@RequestMapping("/user/")
public class SysUserControl {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private SysUserRoleService sysUserRoleService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private SysAuthorityService sysAuthorityService;

    @ReqIdempotent
    @ApiOperation(value = "添加用户", response = RestMessageVo.class)
    @ApiImplicitParam(name = "sign", value = "签名", paramType = "header", dataType = "string")
    @PostMapping("save")
    public JSONObject save(@RequestBody @Valid SysUser sysUser) {
        RestMessage result = sysUserService.save(sysUser);
        return JsonResult.toJSONObj(result);
    }

    @ApiOperation(value = "删除用户", response = RestMessageVo.class)
    @DeleteMapping("removeById/{id}")
    public JSONObject removeById(@PathVariable String id) {
        if (StringUtils.isEmpty(id)) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        RestMessage result = sysUserService.removeById(id);
        return JsonResult.toJSONObj(result);
    }

    @Lock(value = "#p0.getId()", remark = "用户信息修改中，请稍后", timeout = 60)
    @ApiOperation(value = "修改用户信息", response = RestMessageVo.class)
    @PutMapping("update")
    public JSONObject update(@RequestBody @Valid SysUser sysUser) {
        if (StringUtils.isEmpty(sysUser.getId())) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        RestMessage result = sysUserService.update(sysUser);
        return JsonResult.toJSONObj(result);
    }

    @ApiOperation(value = "获取用户详情", response = SysUserVo.class)
    @GetMapping("getById")
    public JSONObject getById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        SysUser sysUser = sysUserService.getById(id);
        if (sysUser == null) {
            return JsonResult.toJSONObj("未查到该对象");
        }
        return JsonResult.toJSONObj(sysUser, "");
    }

    @ApiOperation(value = "查询所有用户", response = PageSysUserVo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "1", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示的条数", defaultValue = "10", paramType = "query", dataType = "int")
    })
    @GetMapping("page")
    public JSONObject page(@RequestParam(required = false) String name, @RequestParam(required = false) String sex, @ApiIgnore Pagination page) {
        Pagination pagination = sysUserService.page(name, sex, page);
        return JsonResult.toJSONObj(pagination, "");
    }

    @ApiOperation(value = "登录", response = RestMessageVo.class)
    @PostMapping("login")
    public JSONObject login(HttpServletRequest request, @RequestParam String userName, @RequestParam String passWord, @RequestParam String checkCode) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(passWord)) {
            return JsonResult.toJSONObj("用户名或密码不能为空");
        }
        if (StringUtils.isEmpty(checkCode)) {
            return JsonResult.toJSONObj("请输入验证码");
        }
        RestMessage login = sysUserService.login(request, userName, passWord, checkCode);
        return JsonResult.toJSONObj(login);
    }

    @ApiOperation(value = "登出", response = RestMessageVo.class)
    @GetMapping("logout")
    public JSONObject logout(HttpServletRequest request) {
        RestMessage logout = sysUserService.logout(request);
        return JsonResult.toJSONObj(logout);
    }

    @ApiOperation(value = "修改密码", response = RestMessageVo.class)
    @PostMapping("updatePassWord")
    public JSONObject updatePassWord(@RequestParam String newPassWord, @RequestParam String oldPassWord, @RequestParam String id) {
        if (StringUtils.isEmpty(id)) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        if (StringUtils.isEmpty(newPassWord) || StringUtils.isEmpty(oldPassWord)) {
            return JsonResult.toJSONObj("原密码或新密码不能为空");
        }
        RestMessage restMessage = sysUserService.updatePassWord(newPassWord, oldPassWord, id);
        return JsonResult.toJSONObj(restMessage);
    }

    @ApiOperation(value = "重置密码", response = RestMessageVo.class)
    @GetMapping("resetPassWord")
    public JSONObject resetPassWord(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        RestMessage restMessage = sysUserService.resetPassWord(id);
        return JsonResult.toJSONObj(restMessage);
    }

    @ApiOperation(value = "获取当前登录用户信息")
    @GetMapping("getLoginUserInfo")
    public JSONObject getLoginUserInfo() {
        String userName = ThreadLocalUtil.getUser();
        SysUser sysUser = sysUserService.getByUserName(userName);
        if (sysUser == null) {
            return JsonResult.toJSONObj("未获取到当前登录用户信息");
        }
        List<SysAuthority> button = new ArrayList<>();
        List<SysAuthority> menu = new ArrayList<>();
        List<SysAuthority> list;
        if (YesOrNoEnum.YES.getCode().equalsIgnoreCase(sysUser.getIsAdmin())) {
            list = sysAuthorityService.listByEnable(YesOrNoEnum.YES.getCode());
        } else {
            list = sysUserService.listAuthorityByIdAndEnable(sysUser.getId(), YesOrNoEnum.YES.getCode());
        }
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(sysAuthority -> {
                if (sysAuthority.getType() == MenuOrButtonEnum.MENU.getCode()) {
                    menu.add(sysAuthority);
                } else {
                    button.add(sysAuthority);
                }
            });
        }
        sysUser.setMenus(menu);
        sysUser.setButtons(button);
        return JsonResult.toJSONObj(sysUser, "");
    }

    @ApiOperation(value = "保存用户角色", response = RestMessageVo.class)
    @PostMapping("saveRole")
    public JSONObject saveRole(@RequestParam String roleIds, @RequestParam String userId) {
        if (StringUtils.isEmpty(roleIds)) {
            return JsonResult.toJSONObj("角色主键不能为空");
        }
        if (StringUtils.isEmpty(userId)) {
            return JsonResult.toJSONObj("用户主键不能为空");
        }
        SysUser old = sysUserService.getById(userId);
        if (old == null) {
            return JsonResult.toJSONObj("该用户不存在");
        }
        if (YesOrNoEnum.YES.getCode().equalsIgnoreCase(old.getIsAdmin())) {
            return JsonResult.toJSONObj("不能对管理员进行操作");
        }
        String[] ids = roleIds.split(",");
        List<SysUserRole> list = new ArrayList<>();
        SysUserRole userRole;
        for (String id : ids) {
            userRole = new SysUserRole();
            userRole.setRoleId(id);
            userRole.setUserId(userId);
            list.add(userRole);
        }
        RestMessage restMessage = sysUserRoleService.saveBatch(list, userId);
        return JsonResult.toJSONObj(restMessage);
    }

    @ApiOperation(value = "根据用户主键查找所拥有的角色", response = ListSysRoleVo.class)
    @GetMapping("listRoleByUserId")
    public JSONObject listRoleByUserId(@RequestParam String userId, @RequestParam(required = false) String enable) {
        if (StringUtils.isEmpty(userId)) {
            return JsonResult.toJSONObj("用户主键不能为空");
        }
        List<SysRole> list = sysRoleService.listByUserIdEnable(userId, enable);
        return JsonResult.toJSONObj(list, "");
    }

    @ApiOperation(value = "根据用户主键查找所拥有的权限", response = ListSysAuthorityVo.class)
    @GetMapping("listAuthorityByUserId")
    public JSONObject listAuthorityByUserId(@RequestParam String userId, @RequestParam(required = false) String enable) {
        if (StringUtils.isEmpty(userId)) {
            return JsonResult.toJSONObj("用户主键不能为空");
        }
        List<SysAuthority> list = sysUserService.listAuthorityByIdAndEnable(userId, enable);
        return JsonResult.toJSONObj(list, "");
    }

    @ApiOperation(value = "导出用户excel表")
    @GetMapping("exportReport")
    public void exportReport(HttpServletResponse response) {
        sysUserService.exportReport(response);
    }
}
