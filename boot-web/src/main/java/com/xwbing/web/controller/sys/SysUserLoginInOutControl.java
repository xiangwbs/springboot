package com.xwbing.web.controller.sys;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.xwbing.service.domain.entity.dto.InAndOutPageDto;
import com.xwbing.service.domain.entity.sys.SysUserLoginInOut;
import com.xwbing.service.domain.entity.vo.InAndOutCountByDateVo;
import com.xwbing.service.domain.entity.vo.InAndOutCountByUserVo;
import com.xwbing.service.service.sys.SysUserLoginInOutService;
import com.xwbing.service.util.PageVO;
import com.xwbing.starter.aspect.annotation.OperateLog;
import com.xwbing.web.response.ApiResponse;
import com.xwbing.web.response.ApiResponseUtil;

import cn.hutool.core.date.DatePattern;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2017/11/7 11:16
 * 作者: xiangwb
 * 说明: 用户登录登出控制层
 */
@Api(tags = "inoutApi", description = "登陆登出相关接口")
@RestController
@RequestMapping("/inout/")
public class SysUserLoginInOutControl {
    @Resource
    private SysUserLoginInOutService inOutService;

    @OperateLog(name = "获取登录或登出信息",description = "'查询登录类型为:'+#pageDto.inout")
    @ApiOperation("获取登录或登出信息")
    @PostMapping("page")
    public ApiResponse<PageVO<SysUserLoginInOut>> page(@RequestBody @Validated InAndOutPageDto pageDto) {
        PageVO<SysUserLoginInOut> page = inOutService.page(pageDto);
        return ApiResponseUtil.success(page);
    }

    @ApiOperation(value = "获取饼图数据")
    @GetMapping("pie")
    public ApiResponse pie(@RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        JSONArray pie = inOutService.pie(startDate, endDate);
        return ApiResponseUtil.success(pie);
    }

    @ApiOperation(value = "获取柱状图数据")
    @GetMapping("bar")
    public ApiResponse bar(@RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> bar = inOutService.bar(startDate, endDate);
        return ApiResponseUtil.success(bar);
    }

    @ApiOperation(value = "countByDate")
    @GetMapping("countByDate")
    public ApiResponse<List<InAndOutCountByDateVo>> countByDate(@RequestParam int inoutType,
            @RequestParam(required = false) @DateTimeFormat(pattern = DatePattern.NORM_DATE_PATTERN) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = DatePattern.NORM_DATE_PATTERN) LocalDate endDate) {
        List<InAndOutCountByDateVo> countByDate = inOutService.countByDate(inoutType, startDate, endDate);
        return ApiResponseUtil.success(countByDate);
    }

    @ApiOperation(value = "countByUser")
    @GetMapping("countByUser")
    public ApiResponse<PageVO<InAndOutCountByUserVo>> countByUser(@RequestParam int inoutType,
            @RequestParam int currentPage, @RequestParam int pageSize) {
        PageVO<InAndOutCountByUserVo> pagination = inOutService.countByUser(inoutType, currentPage, pageSize);
        return ApiResponseUtil.success(pagination);
    }
}
