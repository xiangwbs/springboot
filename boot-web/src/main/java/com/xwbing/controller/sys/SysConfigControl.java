package com.xwbing.controller.sys;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.annotation.LogInfo;
import com.xwbing.domain.entity.model.EmailModel;
import com.xwbing.domain.entity.vo.RestMessageVo;
import com.xwbing.service.sys.CommonConfigService;
import com.xwbing.util.JsonResult;
import com.xwbing.util.RestMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 说明: 系统配置控制层
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Api(tags = "configApi", description = "系统配置相关接口")
@RestController
@RequestMapping("/config/")
public class SysConfigControl {
    @Resource
    private CommonConfigService commonConfigService;

    @LogInfo("获取邮箱配置信息")
    @ApiOperation(value = "获取邮箱配置信息", response = RestMessageVo.class)
    @GetMapping("getEmail")
    public JSONObject getEmail() {
        EmailModel email = commonConfigService.getEmail();
        return JsonResult.toJSONObj(email, "");
    }

    @LogInfo("添加/修改邮箱配置信息")
    @ApiOperation(value = "添加/修改邮箱配置信息", response = RestMessageVo.class)
    @PostMapping("saveOrUpdateEmail")
    public JSONObject saveOrUpdateEmail(@RequestBody EmailModel emailModel) {
        RestMessage restMessage = commonConfigService.saveOrUpdateEmail(emailModel);
        return JsonResult.toJSONObj(restMessage);
    }
}
