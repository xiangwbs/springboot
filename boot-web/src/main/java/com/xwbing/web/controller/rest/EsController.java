package com.xwbing.web.controller.rest;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.service.demo.es.UserEsDTO;
import com.xwbing.service.demo.es.UserEsService;
import com.xwbing.service.demo.es.UserEsVO;
import com.xwbing.service.service.BaseEsService;
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
    private final UserEsService userEsService;
    private final BaseEsService baseEsService;

    public EsController(UserEsService userEsService, BaseEsService baseEsService) {
        this.userEsService = userEsService;
        this.baseEsService = baseEsService;
    }

    @ApiOperation("新增或修改")
    @PostMapping("/upsert")
    public ApiResponse upsert(@RequestBody UserEsVO dto) {
        userEsService.upsert(dto);
        return ApiResponseUtil.success();
    }

    @ApiOperation("新增或修改")
    @PostMapping("/bulkUpsert")
    public ApiResponse bulkUpsert(@RequestBody UserEsVO dto) {
        userEsService.bulkUpsert(Collections.singletonList(dto));
        return ApiResponseUtil.success();
    }

    @ApiOperation("删除")
    @GetMapping("/delete")
    public ApiResponse delete(@RequestParam Long id) {
        baseEsService.delete(id, UserEsService.INDEX);
        return ApiResponseUtil.success();
    }

    @ApiOperation("删除")
    @GetMapping("/bulkDelete")
    public ApiResponse bulkDelete(@RequestParam List<Long> ids) {
        baseEsService.bulkDelete(ids, UserEsService.INDEX);
        return ApiResponseUtil.success();
    }

    @ApiOperation("详情")
    @GetMapping("/getById")
    public ApiResponse<UserEsVO> getById(@RequestParam Long id) {
        return ApiResponseUtil.success(baseEsService.get(id, UserEsVO.class, UserEsService.INDEX));
    }

    @ApiOperation("列表")
    @GetMapping("/listByIds")
    public ApiResponse<List<UserEsVO>> listByIds(@RequestParam List<Long> ids) {
        return ApiResponseUtil.success(baseEsService.mget(ids, UserEsVO.class, UserEsService.INDEX));
    }

    @ApiOperation("用户列表")
    @PostMapping("/searchUser")
    public ApiResponse<PageVO<UserEsVO>> searchUser(@RequestBody UserEsDTO dto) {
        return ApiResponseUtil.success(userEsService.searchUser(dto));
    }
}
