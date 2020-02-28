package com.xwbing.controller.sys;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.config.annotation.FlowLimiter;
import com.xwbing.config.annotation.Idempotent;
import com.xwbing.annotation.LogInfo;
import com.xwbing.constant.CommonConstant;
import com.xwbing.constant.CommonEnum;
import com.xwbing.domain.entity.sys.SysAuthority;
import com.xwbing.domain.entity.vo.ListSysAuthorityVo;
import com.xwbing.domain.entity.vo.PageSysAuthorityVo;
import com.xwbing.domain.entity.vo.RestMessageVo;
import com.xwbing.domain.entity.vo.SysAuthVo;
import com.xwbing.service.sys.SysAuthorityService;
import com.xwbing.util.CommonDataUtil;
import com.xwbing.util.JsonResult;
import com.xwbing.util.Pagination;
import com.xwbing.util.RestMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2017/11/15 10:36
 * 作者: xiangwb
 * 说明: 权限控制层
 */
@Api(tags = "authorityApi", description = "权限相关接口")
@RestController
@RequestMapping("/authority/")
public class SysAuthorityControl {
    private final Lock lock = new ReentrantLock(true);
    @Resource
    private SysAuthorityService sysAuthorityService;

    @Idempotent
    @LogInfo("添加权限")
    @ApiOperation(value = "添加权限", response = RestMessageVo.class)
    @ApiImplicitParam(name = "sign", value = "签名", paramType = "header", dataType = "string")
    @PostMapping("save")
    public JSONObject save(@RequestBody SysAuthority sysAuthority) throws InterruptedException {
        //删除缓存
        CommonDataUtil.clearData(CommonConstant.AUTHORITY_THREE);
//        redisService.del(CommonConstant.AUTHORITY_THREE);
        RestMessage save = sysAuthorityService.save(sysAuthority);
        //延时双删,时间根据读数据业务逻辑的耗时
        Thread.sleep(500);
        CommonDataUtil.clearData(CommonConstant.AUTHORITY_THREE);
        return JsonResult.toJSONObj(save);
    }

    @LogInfo("删除权限")
    @ApiOperation(value = "删除权限", response = RestMessageVo.class)
    @DeleteMapping("removeById/{id}")
    public JSONObject removeById(@PathVariable String id) throws InterruptedException {
        if (StringUtils.isEmpty(id)) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        //删除缓存
        CommonDataUtil.clearData(CommonConstant.AUTHORITY_THREE);
//        redisService.del(CommonConstant.AUTHORITY_THREE);
        RestMessage result = sysAuthorityService.removeById(id);
        Thread.sleep(500);
        CommonDataUtil.clearData(CommonConstant.AUTHORITY_THREE);
        return JsonResult.toJSONObj(result);
    }

    @LogInfo("修改权限")
    @ApiOperation(value = "修改权限", response = RestMessageVo.class)
    @PutMapping("update")
    public JSONObject update(@RequestBody SysAuthority sysAuthority) throws InterruptedException {
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
        //删除缓存
        CommonDataUtil.clearData(CommonConstant.AUTHORITY_THREE);
//        redisService.del(CommonConstant.AUTHORITY_THREE);
        RestMessage result = sysAuthorityService.update(sysAuthority);
        Thread.sleep(500);
        CommonDataUtil.clearData(CommonConstant.AUTHORITY_THREE);
        return JsonResult.toJSONObj(result);
    }

    @LogInfo("根据是否启用分页查询所有权限")
    @ApiOperation(value = "根据是否启用分页查询所有权限", response = PageSysAuthorityVo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页", defaultValue = "1", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示的条数", defaultValue = "10", paramType = "query", dataType = "int")
    })
    @GetMapping("pageByEnable")
    public JSONObject pageByEnable(@RequestParam(required = false) String enable, @ApiIgnore Pagination page) {
        Pagination pagination = sysAuthorityService.pageByEnable(enable, page);
        return JsonResult.toJSONObj(pagination, "");
    }

    @LogInfo("根据父节点查询子节点")
    @ApiOperation(value = "根据父节点查询子节点", response = ListSysAuthorityVo.class)
    @GetMapping("listByParentId")
    public JSONObject listByParentId(@RequestParam(required = false) String parentId) {
        if (StringUtils.isEmpty(parentId)) {
            parentId = CommonConstant.ROOT;
        }
        if (!CommonConstant.ROOT.equals(parentId) && sysAuthorityService.getById(parentId) == null) {
            return JsonResult.toJSONObj("父节点不存在");
        }
        List<SysAuthority> queryByParentId = sysAuthorityService.listByParentEnable(parentId, null);
        return JsonResult.toJSONObj(queryByParentId, "");
    }

    @FlowLimiter(permitsPerSecond = 100, timeOut = 500)
    @LogInfo("递归查询所有权限")
    @ApiOperation(value = "递归查询所有权限", response = ListSysAuthorityVo.class)
    @GetMapping("listTree")
    public JSONObject listTree(@RequestParam(required = false) String enable) throws InterruptedException {//互斥锁解决缓存击穿问题
        //先去缓存里拿
        List<SysAuthVo> authVos = (List<SysAuthVo>) CommonDataUtil.getData(CommonConstant.AUTHORITY_THREE);
        if (authVos == null) {
            //去获取锁,获取成功,去数据库取数据
            if (lock.tryLock()) {
                try {
                    authVos = (List<SysAuthVo>) CommonDataUtil.getData(CommonConstant.AUTHORITY_THREE);//应对高并发场景
                    if (authVos != null) {
                        return JsonResult.toJSONObj(authVos, "");
                    }
                    authVos = sysAuthorityService.listChildren(CommonConstant.ROOT, enable);
                    if (CollectionUtils.isNotEmpty(authVos)) {
                        // 设置缓存
                        CommonDataUtil.setData(CommonConstant.AUTHORITY_THREE, authVos);
                    }
                } finally {
                    lock.unlock();
                }
                //获取锁失败
            } else {
                //先查一下缓存
                authVos = (List<SysAuthVo>) CommonDataUtil.getData(CommonConstant.AUTHORITY_THREE);
                //没拿到锁,缓存也没数据,先小憩一下
                if (CollectionUtils.isNotEmpty(authVos)) {
                    Thread.sleep(100);
                    return listTree(enable);
                }
            }
        }
        return JsonResult.toJSONObj(authVos, "");
    }
}
