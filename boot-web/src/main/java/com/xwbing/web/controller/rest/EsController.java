package com.xwbing.web.controller.rest;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.service.demo.es.ArticleEsDTO;
import com.xwbing.service.demo.es.ArticleEsService;
import com.xwbing.service.demo.es.ArticleEsVO;
import com.xwbing.service.service.EsHelper;
import com.xwbing.service.util.PageVO;
import com.xwbing.web.response.ApiResponse;
import com.xwbing.web.response.ApiResponseUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2022年07月05日 3:06 PM
 */
@Slf4j
@Api(tags = "es", description = "es相关")
@RestController
@RequestMapping("/es/")
public class EsController {
    private final ArticleEsService articleEsService;
    private final EsHelper esHelper;

    public EsController(ArticleEsService articleEsService, EsHelper esHelper) {
        this.articleEsService = articleEsService;
        this.esHelper = esHelper;
    }

    @ApiOperation("新增或修改")
    @PostMapping("/upsert")
    public ApiResponse upsert(@RequestBody ArticleEsVO dto) {
        articleEsService.upsert(dto);
        return ApiResponseUtil.success();
    }

    @ApiOperation("新增或修改")
    @PostMapping("/bulkUpsert")
    public ApiResponse bulkUpsert(@RequestBody ArticleEsVO dto) {
        articleEsService.bulkUpsert(Collections.singletonList(dto));
        return ApiResponseUtil.success();
    }

    @ApiOperation("删除")
    @GetMapping("/delete")
    public ApiResponse delete(@RequestParam Long id) {
        esHelper.delete(String.valueOf(id), ArticleEsService.INDEX);
        return ApiResponseUtil.success();
    }

    @ApiOperation("删除")
    @GetMapping("/bulkDelete")
    public ApiResponse bulkDelete(@RequestParam List<String> ids) {
        esHelper.bulkDelete(ids, ArticleEsService.INDEX);
        return ApiResponseUtil.success();
    }

    @ApiOperation("详情")
    @GetMapping("/get")
    public ApiResponse<ArticleEsVO> get(@RequestParam Long id) {
        return ApiResponseUtil.success(esHelper.get(String.valueOf(id), ArticleEsVO.class, ArticleEsService.INDEX));
    }

    @ApiOperation("列表")
    @GetMapping("/mget")
    public ApiResponse<List<ArticleEsVO>> mget(@RequestParam List<String> ids) {
        return ApiResponseUtil.success(esHelper.mget(ids, ArticleEsVO.class, ArticleEsService.INDEX));
    }

    @ApiOperation("count")
    @GetMapping("/count")
    public ApiResponse<Integer> count(@RequestParam String issueDeptCode) {
        return ApiResponseUtil.success(articleEsService.count(issueDeptCode));
    }

    @ApiOperation("搜索")
    @PostMapping("/search")
    public ApiResponse<PageVO<ArticleEsVO>> search(@RequestBody ArticleEsDTO dto) {
        return ApiResponseUtil.success(articleEsService.search(dto));
    }
}