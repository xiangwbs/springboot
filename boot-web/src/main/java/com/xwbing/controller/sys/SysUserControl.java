package com.xwbing.controller.sys;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.annotation.LogInfo;
import com.xwbing.constant.CommonEnum;
import com.xwbing.domain.entity.sys.*;
import com.xwbing.domain.entity.vo.*;
import com.xwbing.redis.RedisService;
import com.xwbing.service.sys.*;
import com.xwbing.shiro.UsernamePasswordCaptchaToken;
import com.xwbing.util.IpUtil;
import com.xwbing.util.JsonResult;
import com.xwbing.util.RestMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 说明: 用户控制层
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Api(tags = "sysUserApi", description = "用户相关接口")
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
    @Resource
    private RedisService redisService;
    @Resource
    private SysUserLoginInOutService loginInOutService;

    @LogInfo("添加用户")
    @ApiOperation(value = "添加用户", response = RestMessageVo.class)
    @PostMapping("save")
    public JSONObject save(@RequestBody @Valid SysUser sysUser) {
        RestMessage result = sysUserService.save(sysUser);
        return JsonResult.toJSONObj(result);
    }

    @LogInfo("删除用户")
    @ApiOperation(value = "删除用户", response = RestMessageVo.class)
    @GetMapping("removeById")
    public JSONObject removeById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        RestMessage result = sysUserService.removeById(id);
        return JsonResult.toJSONObj(result);
    }

    @LogInfo("修改用户信息")
    @ApiOperation(value = "修改用户信息", response = RestMessageVo.class)
    @PostMapping("update")
    public JSONObject update(@RequestBody @Valid SysUser sysUser) {
        if (StringUtils.isEmpty(sysUser.getId())) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        RestMessage result = sysUserService.update(sysUser);
        return JsonResult.toJSONObj(result);
    }

    @LogInfo("获取用户详情")
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

    @LogInfo("列表查询所有用户")
    @ApiOperation(value = "列表查询所有用户", response = ListSysUserVo.class)
    @GetMapping("listAll")
    public JSONObject listAll() {
        List<SysUser> list = sysUserService.listAll();
        return JsonResult.toJSONObj(list, "");
    }

    @LogInfo("登录")
    @ApiOperation(value = "登录", response = RestMessageVo.class)
    @GetMapping("login")
    public JSONObject login(HttpServletRequest request,@RequestParam boolean rememberMe, @RequestParam String userName, @RequestParam String passWord, @RequestParam String captcha) {
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(passWord)) {
            return JsonResult.toJSONObj("用户名或密码不能为空");
        }
        Subject subject = SecurityUtils.getSubject();
        String ip = IpUtil.getIpAddr(request);
        UsernamePasswordCaptchaToken token = new UsernamePasswordCaptchaToken(userName, passWord.toCharArray(), rememberMe, ip, captcha);
        subject.login(token);
        if (subject.isAuthenticated()) {
            //保存登录信息
            SysUser user = (SysUser) subject.getPrincipal();
            SysUserLoginInOut loginInOut = new SysUserLoginInOut();
            loginInOut.setCreateTime(new Date());
            loginInOut.setUserId(user.getId());
            loginInOut.setInoutType(CommonEnum.LoginInOutEnum.IN.getValue());
            loginInOut.setIp(ip);
            RestMessage save = loginInOutService.save(loginInOut);
            if (!save.isSuccess()) {
                subject.logout();
                return JsonResult.toJSONObj("保存用户登录日志失败");
            }else {
                return JsonResult.toJSONObj(new Object(), "登录成功");
            }
        }else {
            return JsonResult.toJSONObj("你认证未通过,请重新登录");
        }
    }

    @LogInfo("登出")
    @ApiOperation(value = "登出", response = RestMessageVo.class)
    @GetMapping("logout")
    public JSONObject logout(HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null && subject.getPrincipal() != null) {
            SysUser sysUser = (SysUser) subject.getPrincipal();
            if (null != sysUser) {
                SysUserLoginInOut loginInOut = new SysUserLoginInOut();
                loginInOut.setCreateTime(new Date());
                loginInOut.setUserId(sysUser.getId());
                loginInOut.setInoutType(CommonEnum.LoginInOutEnum.OUT.getValue());
                loginInOut.setIp(IpUtil.getIpAddr(request));
                RestMessage out = loginInOutService.save(loginInOut);
                if (out.isSuccess()) {
                    subject.logout();
                    return JsonResult.toJSONObj(new Object(), "登出成功");
                }else {
                    return JsonResult.toJSONObj("保存用户登出信息失败");
                }
            }
        }
        return JsonResult.toJSONObj("没有获取到用户登录信息");
    }

    @LogInfo("当前用户修改密码")
    @ApiOperation(value = "当前用户修改密码", response = RestMessageVo.class)
    @PostMapping("updatePassWord")
    public JSONObject updatePassWord(@RequestParam String newPassWord, @RequestParam String oldPassWord) {
        SysUser currentInfo = sysUserService.getCurrentInfo();
        if(currentInfo==null){
            return JsonResult.toJSONObj("没有获取到当前登录用户信息");
        }
        if (StringUtils.isEmpty(newPassWord) || StringUtils.isEmpty(oldPassWord)) {
            return JsonResult.toJSONObj("原密码或新密码不能为空");
        }
        RestMessage restMessage = sysUserService.updatePassWord(newPassWord, oldPassWord, currentInfo.getId());
        return JsonResult.toJSONObj(restMessage);
    }

    @LogInfo("重置密码")
    @ApiOperation(value = "重置密码", response = RestMessageVo.class)
    @GetMapping("resetPassWord")
    public JSONObject resetPassWord(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        RestMessage restMessage = sysUserService.resetPassWord(id);
        return JsonResult.toJSONObj(restMessage);
    }

    @LogInfo("获取当前登录用户信息")
    @ApiOperation(value = "获取当前登录用户信息")
    @GetMapping("getLoginUserInfo")
    public JSONObject getLoginUserInfo() {
        SysUser sysUser = sysUserService.getCurrentInfo();
        if (sysUser == null) {
            return JsonResult.toJSONObj("未获取到当前登录用户信息");
        }
        List<SysAuthority> button = new ArrayList<>();
        List<SysAuthority> menu = new ArrayList<>();
        List<SysAuthority> list;
        //如果是管理员，获取全部权限。否则查询所拥有的权限
        if (CommonEnum.YesOrNoEnum.YES.getCode().equalsIgnoreCase(sysUser.getIsAdmin())) {
            list = sysAuthorityService.listByEnable(CommonEnum.YesOrNoEnum.YES.getCode());
        } else {
            list = sysUserService.listAuthorityByIdAndEnable(sysUser.getId(), CommonEnum.YesOrNoEnum.YES.getCode());
        }
        if (CollectionUtils.isNotEmpty(list)) {
            for (SysAuthority sysAuthority : list) {
                if (sysAuthority.getType() == CommonEnum.MenuOrButtonEnum.MENU.getCode()) {
                    menu.add(sysAuthority);
                } else {
                    button.add(sysAuthority);
                }
            }
        }
        sysUser.setMenus(menu);
        sysUser.setButtons(button);
        return JsonResult.toJSONObj(sysUser, "");
    }

    @LogInfo("保存用户角色")
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
        if (CommonEnum.YesOrNoEnum.YES.getCode().equalsIgnoreCase(old.getIsAdmin())) {
            return JsonResult.toJSONObj("不能对管理员进行操作");
        }
        String ids[] = roleIds.split(",");
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

    @LogInfo("根据用户主键查找所拥有的角色")
    @ApiOperation(value = "根据用户主键查找所拥有的角色", response = ListSysRoleVo.class)
    @ApiImplicitParam(name = "enable", value = "是否启用,格式Y|N", paramType = "query", dataType = "string")
    @PostMapping("listRoleByUserId")
    public JSONObject listRoleByUserId(@RequestParam String userId, String enable) {
        if (StringUtils.isEmpty(userId)) {
            return JsonResult.toJSONObj("用户主键不能为空");
        }
        List<SysRole> list = sysRoleService.listByUserIdEnable(userId, enable);
        return JsonResult.toJSONObj(list, "");
    }

    @LogInfo("根据用户主键查找所拥有的权限")
    @ApiOperation(value = "根据用户主键查找所拥有的权限", response = ListSysAuthorityVo.class)
    @ApiImplicitParam(name = "enable", value = "是否启用,格式Y|N", paramType = "query", dataType = "string")
    @PostMapping("listAuthorityByUserId")
    public JSONObject listAuthorityByUserId(@RequestParam String userId, String enable) {
        if (StringUtils.isEmpty(userId)) {
            return JsonResult.toJSONObj("用户主键不能为空");
        }
        List<SysAuthority> list = sysUserService.listAuthorityByIdAndEnable(userId, enable);
        return JsonResult.toJSONObj(list, "");
    }

    @LogInfo("导出用户excel表")
    @ApiOperation(value = "导出用户excel表")
    @GetMapping("exportReport")
    public void exportReport(HttpServletResponse response) {
        sysUserService.exportReport(response);
    }
}
