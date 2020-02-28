package com.xwbing.controller.sys;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwbing.annotation.LogInfo;
import com.xwbing.constant.CommonConstant;
import com.xwbing.constant.CommonEnum;
import com.xwbing.domain.entity.sys.SysAuthority;
import com.xwbing.domain.entity.vo.ListSysAuthorityVo;
import com.xwbing.domain.entity.vo.RestMessageVo;
import com.xwbing.domain.entity.vo.SysAuthVo;
import com.xwbing.redis.RedisService;
import com.xwbing.service.sys.SysAuthorityService;
import com.xwbing.util.JsonResult;
import com.xwbing.util.RestMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2017/11/15 10:36
 * 作者: xiangwb
 * 说明: 权限控制层
 */
@Api(tags = "authorityApi", description = "权限相关接口")
@RestController
@RequestMapping("/authority/")
public class SysAuthorityControl {
    @Resource
    private SysAuthorityService sysAuthorityService;
    @Resource
    private RedisService redisService;

    @LogInfo("添加权限")
    @PostMapping("save")
    @ApiOperation(value = "添加权限", response = RestMessageVo.class)
    public JSONObject save(@RequestBody SysAuthority sysAuthority) {
        RestMessage save = sysAuthorityService.save(sysAuthority);
        //删除缓存
        if (save.isSuccess()) {
            redisService.del(CommonConstant.AUTHORITY_THREE);
        }
        return JsonResult.toJSONObj(save);
    }

    @LogInfo("删除权限")
    @ApiOperation(value = "删除权限", response = RestMessageVo.class)
    @GetMapping("removeById")
    public JSONObject removeById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        RestMessage result = sysAuthorityService.removeById(id);
        //删除缓存
        if (result.isSuccess()) {
            redisService.del(CommonConstant.AUTHORITY_THREE);
        }
        return JsonResult.toJSONObj(result);
    }

    @LogInfo("修改权限")
    @ApiOperation(value = "修改权限", response = RestMessageVo.class)
    @PostMapping("update")
    public JSONObject update(@RequestBody SysAuthority sysAuthority) {
        if (StringUtils.isEmpty(sysAuthority.getId())) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        String enable = sysAuthority.getEnable();
        // 如果禁用，查询是否有子节点，如果有，子节点也要被禁用
        if (CommonConstant.IS_NOT_ENABLE.equals(enable)) {
            // 子节点的权限都禁用
            if (!sysAuthorityService.disableChildrenByParentId(sysAuthority.getId())) {
                return JsonResult.toJSONObj("禁用子节点权限失败");
            }
        } else {
            // 如果是启用,看父节点是否被禁用,一级的不需要判断
            if (!CommonConstant.ROOT.equals(sysAuthority.getParentId())) {
                SysAuthority parent = sysAuthorityService.getById(sysAuthority.getParentId());
                if (parent != null && CommonEnum.YesOrNoEnum.NO.getCode().equals(parent.getEnable())) {
                    return JsonResult.toJSONObj("父节点已被禁用，请先启用父节点");
                }
            }
        }
        RestMessage result = sysAuthorityService.update(sysAuthority);
        //删除缓存
        if (result.isSuccess()) {
            redisService.del(CommonConstant.AUTHORITY_THREE);
        }
        return JsonResult.toJSONObj(result);
    }

    @LogInfo("根据是否启用查询所有权限")
    @ApiOperation(value = "根据是否启用查询所有权限", response = ListSysAuthorityVo.class)
    @ApiImplicitParam(name = "enable", value = "是否启用(Y|N)", paramType = "query", dataType = "string")
    @GetMapping("listByEnable")
    public JSONObject listByEnable(String enable) {
        List<SysAuthority> authoritys = sysAuthorityService.listByEnable(enable);
        return JsonResult.toJSONObj(authoritys, "");
    }

    @LogInfo("根据父节点查询子节点")
    @GetMapping("listByParentId")
    @ApiOperation(value = "根据父节点查询子节点", response = ListSysAuthorityVo.class)
    @ApiImplicitParam(name = "parentId", value = "父id,可为空", paramType = "query", dataType = "string")
    public JSONObject listByParentId(String parentId) {
        if (StringUtils.isEmpty(parentId)) {
            parentId = CommonConstant.ROOT;
        }
        if (!CommonConstant.ROOT.equals(parentId) && sysAuthorityService.getById(parentId) == null) {
            return JsonResult.toJSONObj("父节点不存在");
        }
        List<SysAuthority> queryByParentId = sysAuthorityService.listByParentEnable(parentId, null);
        return JsonResult.toJSONObj(queryByParentId, "");
    }

    @LogInfo("递归查询所有权限")
    @ApiOperation(value = "递归查询所有权限", response = ListSysAuthorityVo.class)
    @ApiImplicitParam(name = "enable", value = "是否启用(Y|N)", paramType = "query", dataType = "string")
    @GetMapping("listTree")
    public JSONObject listTree(String enable) {
        List<SysAuthVo> authoritys;
        //先去缓存里拿
        boolean exists = redisService.exists(CommonConstant.AUTHORITY_THREE);
        if (exists) {
            String result = redisService.get(CommonConstant.AUTHORITY_THREE);
            authoritys = JSONArray.parseArray(result, SysAuthVo.class);
        } else {
            authoritys = sysAuthorityService.listChildren(CommonConstant.ROOT, enable);
            // 设置缓存
            redisService.set(CommonConstant.AUTHORITY_THREE, JSONArray.toJSONString(authoritys));
        }
        return JsonResult.toJSONObj(authoritys, "");
    }
}
