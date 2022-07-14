package com.xwbing.web.controller.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.service.demo.es.EsDemo;
import com.xwbing.service.demo.es.UserEsDTO;
import com.xwbing.service.demo.es.UserEsVO;
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
    private final EsDemo esDemo;

    public EsController(EsDemo esDemo) {
        this.esDemo = esDemo;
    }

    @ApiOperation("详情")
    @GetMapping("/getById")
    public ApiResponse<UserEsVO> getById(@RequestParam Long id) {
        return ApiResponseUtil.success(esDemo.getById(id, UserEsVO.class, EsDemo.INDEX));
    }

    @ApiOperation("列表")
    @GetMapping("/listByIds")
    public ApiResponse<List<UserEsVO>> listByIds(@RequestParam List<Long> ids) {
        return ApiResponseUtil.success(esDemo.listByIds(ids, UserEsVO.class, EsDemo.INDEX));
    }

    @ApiOperation("用户列表")
    @PostMapping("/searchUser")
    public ApiResponse<PageVO<UserEsVO>> searchUser(@RequestBody UserEsDTO dto) {
        return ApiResponseUtil.success(esDemo.searchUser(dto));
    }
}
