package com.xwbing.web.controller.rest;

import cn.hutool.core.lang.tree.Tree;
import com.xwbing.service.domain.entity.rest.Xzqh;
import com.xwbing.service.service.rest.XzqhService;
import com.xwbing.web.response.ApiResponse;
import com.xwbing.web.response.ApiResponseUtil;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author daofeng
 * @version $
 * @since 2024年08月15日 3:57 PM
 */
@Api(tags = "行政区划")
@RestController
@RequestMapping("/xzqh/")
public class XzqhController {
    @Resource
    private XzqhService xzqhService;

    @GetMapping("/getGeo")
    public ApiResponse<String> getGeo(@RequestParam List<String> regionList) {
        return ApiResponseUtil.success(xzqhService.getGeo(regionList));
    }

    @GetMapping("/tree")
    public ApiResponse<List<Tree<String>>> tree() {
        return ApiResponseUtil.success(xzqhService.tree());
    }

    @GetMapping("/tree1")
    public ApiResponse<List<Xzqh>> tree1() {
        return ApiResponseUtil.success(xzqhService.tree1());
    }
}