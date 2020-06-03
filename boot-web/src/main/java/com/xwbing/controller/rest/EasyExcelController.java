package com.xwbing.controller.rest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.domain.entity.vo.ExcelProcessVo;
import com.xwbing.service.rest.EasyExcelDealService;
import com.xwbing.service.rest.ImportTaskService;
import com.xwbing.util.JsonResult;
import com.xwbing.util.Pagination;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月28日 上午11:48
 */
@Api(tags = "easyExcelController", description = "excel相关接口")
@RestController
@RequestMapping("/excel/")
public class EasyExcelController {
    @Resource
    private EasyExcelDealService easyExcelDealService;
    @Resource
    private ImportTaskService importTaskService;

    @ApiOperation(value = "批量导入")
    @PostMapping("import")
    public JSONObject readExcel(@RequestParam MultipartFile file) {
        String sign = easyExcelDealService.readByStream(file, 0, 1);
        return JsonResult.toJSONObj(sign, "");
    }

    @ApiOperation("读取excel进度")
    @GetMapping("getProcess")
    public JSONObject read(@RequestParam String importId) {
        ExcelProcessVo excelProgress = easyExcelDealService.getProcess(importId);
        return JsonResult.toJSONObj(excelProgress, "");
    }

    @ApiOperation("下载导入失败记录")
    @GetMapping("downloadFailRecord")
    public void downloadFailRecord(HttpServletResponse response, @RequestParam String importId) {
        easyExcelDealService.writeToBrowser(response, importId);
    }

    @ApiOperation("导入记录")
    @GetMapping("pageImport")
    public JSONObject pageImport(Pagination page) {
        Pagination pagination = importTaskService.page(page);
        return JsonResult.toJSONObj(pagination, "");
    }
}

