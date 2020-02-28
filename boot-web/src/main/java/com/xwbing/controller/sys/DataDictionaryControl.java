package com.xwbing.controller.sys;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.config.annotation.Idempotent;
import com.xwbing.annotation.LogInfo;
import com.xwbing.constant.CommonConstant;
import com.xwbing.domain.entity.sys.DataDictionary;
import com.xwbing.domain.entity.vo.DataDictionaryVo;
import com.xwbing.domain.entity.vo.ListDataDictionaryVo;
import com.xwbing.domain.entity.vo.RestMessageVo;
import com.xwbing.service.sys.DataDictionaryService;
import com.xwbing.util.JsonResult;
import com.xwbing.util.RestMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2018/2/26 14:09
 * 作者: xiangwb
 * 说明:
 */
@Api(tags = "dictionaryApi", description = "数据字典相关接口")
@RestController
@RequestMapping("/dictionary/")
public class DataDictionaryControl {
    @Resource
    private DataDictionaryService dataDictionaryService;

    @Idempotent
    @LogInfo("添加字典")
    @ApiOperation(value = "添加字典", response = RestMessageVo.class)
    @PostMapping("save")
    public JSONObject save(@RequestBody DataDictionary dictionary) {
        RestMessage result = dataDictionaryService.save(dictionary);
        return JsonResult.toJSONObj(result);
    }

    @LogInfo("修改字典")
    @ApiOperation(value = "修改字典", response = RestMessageVo.class)
    @PutMapping("update")
    public JSONObject update(@RequestBody DataDictionary dictionary) {
        if (StringUtils.isEmpty(dictionary.getId())) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        RestMessage result = dataDictionaryService.update(dictionary);
        return JsonResult.toJSONObj(result);
    }

    @LogInfo("获取字典详情")
    @ApiOperation(value = "获取字典详情", response = DataDictionaryVo.class)
    @GetMapping("getById")
    public JSONObject getById(@RequestParam String id) {
        if (StringUtils.isEmpty(id)) {
            return JsonResult.toJSONObj("主键不能为空");
        }
        DataDictionary dataDictionary = dataDictionaryService.getById(id);
        if (dataDictionary == null) {
            return JsonResult.toJSONObj("该字典不存在");
        }
        return JsonResult.toJSONObj(dataDictionary, "");
    }

    @LogInfo("校验编码")
    @ApiOperation(value = "校验编码", response = RestMessageVo.class)
    @PostMapping("uniqueCode")
    public JSONObject uniqueCode(@RequestParam String code, @RequestParam(required = false) String id) {
        RestMessage result = new RestMessage();
        boolean b = dataDictionaryService.uniqueCode(code, id);
        result.setSuccess(b);
        return JsonResult.toJSONObj(result);
    }

    @LogInfo("根据父id列表查询")
    @ApiOperation(value = "根据父id列表查询", response = ListDataDictionaryVo.class)
    @GetMapping("findListByParent")
    public JSONObject findListByParent(@RequestParam(required = false, defaultValue = "Y") String enable, @RequestParam(required = false) String parentId) {
        if (StringUtils.isEmpty(parentId)) {
            parentId = CommonConstant.ROOT;
        }
        List<DataDictionary> listByParent = dataDictionaryService.findListByParent(parentId, enable);
        return JsonResult.toJSONObj(listByParent, "");
    }

    @LogInfo("根据编码查询字典列表")
    @ApiOperation(value = "根据编码查询字典列表", response = ListDataDictionaryVo.class)
    @GetMapping("findListByCode")
    public JSONObject findListByCode(@RequestParam String code, @RequestParam(required = false, defaultValue = "Y") String enable) {
        if (StringUtils.isEmpty(code)) {
            return JsonResult.toJSONObj("编码不能为空");
        }
        List<DataDictionary> listByParent = dataDictionaryService.findListByCode(code, enable);
        return JsonResult.toJSONObj(listByParent, "");
    }
}
