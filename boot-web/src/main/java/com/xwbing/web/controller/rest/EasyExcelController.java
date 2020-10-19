package com.xwbing.web.controller.rest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xwbing.service.domain.entity.vo.ExcelProcessVo;
import com.xwbing.service.service.rest.EasyExcelDealService;
import com.xwbing.service.service.rest.ImportTaskService;
import com.xwbing.service.util.Pagination;
import com.xwbing.web.response.ApiResponse;
import com.xwbing.web.response.ApiResponseUtil;

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
    public ApiResponse<String> readExcel(@RequestParam MultipartFile file) {
        String importId = easyExcelDealService.readByStream(file, 0, 1);
        return ApiResponseUtil.success(importId);
    }

    @ApiOperation("读取excel进度")
    @GetMapping("getProcess")
    public ApiResponse<ExcelProcessVo> read(@RequestParam String importId) {
        ExcelProcessVo excelProgress = easyExcelDealService.getProcess(importId);
        return ApiResponseUtil.success(excelProgress);
    }

    @ApiOperation("下载导入失败记录")
    @GetMapping("downloadFailRecord")
    public void downloadFailRecord(HttpServletResponse response, @RequestParam String importId) {
        easyExcelDealService.writeToBrowser(response, importId);
    }

    @ApiOperation("导入记录")
    @GetMapping("pageImport")
    public ApiResponse<Pagination> pageImport(Pagination page) {
        Pagination pagination = importTaskService.page(page);
        return ApiResponseUtil.success(pagination);
    }
}

