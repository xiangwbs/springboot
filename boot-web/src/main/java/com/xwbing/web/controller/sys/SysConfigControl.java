package com.xwbing.web.controller.sys;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.domain.entity.model.EmailModel;
import com.xwbing.service.domain.entity.vo.RestMessageVo;
import com.xwbing.service.service.rest.CommonService;
import com.xwbing.service.util.JsonResult;
import com.xwbing.service.util.RestMessage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 说明: 系统配置控制层
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Slf4j
@Api(tags = "configApi", description = "系统配置相关接口")
@RestController
@RequestMapping("/config/")
public class SysConfigControl {
    @Resource
    private CommonService commonService;

    @ApiOperation(value = "获取邮箱配置信息", response = RestMessageVo.class)
    @GetMapping("getEmail")
    public JSONObject getEmail() {
        EmailModel email = commonService.getEmail();
        return JsonResult.toJSONObj(email, "");
    }

    @ApiOperation(value = "添加/修改邮箱配置信息", response = RestMessageVo.class)
    @PostMapping("saveOrUpdateEmail")
    public JSONObject saveOrUpdateEmail(@RequestBody EmailModel emailModel) {
        RestMessage restMessage = commonService.saveOrUpdateEmail(emailModel);
        return JsonResult.toJSONObj(restMessage);
    }
}
