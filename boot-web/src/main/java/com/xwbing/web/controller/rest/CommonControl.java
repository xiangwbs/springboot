package com.xwbing.web.controller.rest;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.domain.entity.vo.RestMessageVo;
import com.xwbing.service.service.rest.CommonService;
import com.xwbing.service.util.JsonResult;
import com.xwbing.service.util.RestMessage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2018/5/7 9:57
 * 作者: xiangwb
 * 说明:
 */
@Api(tags = "commonApi", description = "公共相关接口")
@RestController
@RequestMapping("/common/")
public class CommonControl {
    @Resource
    private CommonService commonService;

    @ApiOperation(value = "获取签名", response = RestMessageVo.class)
    @GetMapping("getSign")
    public JSONObject getSign() {
        String sign = commonService.getSign();
        return JsonResult.toJSONObj(sign, "");
    }

    @ApiOperation("上传文件")
    @PostMapping("upload")
    public JSONObject upload(@RequestParam MultipartFile file) {
        RestMessage restMessage = commonService.upload(file);
        return JsonResult.toJSONObj(restMessage);
    }
}

