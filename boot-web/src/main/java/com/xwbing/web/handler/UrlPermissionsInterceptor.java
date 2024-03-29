package com.xwbing.web.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.xwbing.service.constant.CommonConstant;
import com.xwbing.service.domain.entity.sys.SysAuthority;
import com.xwbing.service.domain.entity.sys.SysUser;
import com.xwbing.service.enums.YesOrNoEnum;
import com.xwbing.service.service.sys.SysAuthorityService;
import com.xwbing.service.service.sys.SysUserService;
import com.xwbing.service.util.RestMessage;
import com.xwbing.starter.util.UserContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlPermissionsInterceptor extends HandlerInterceptorAdapter {
    private static final AntPathMatcher MATCHER = new AntPathMatcher();
    @Resource
    private SysAuthorityService sysAuthorityService;
    @Resource
    private SysUserService sysUserService;
    //@formatter:off
    private static final Set<String> ALLOWED_PATH = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            //映射swagger文档
            "/doc",
            //验证码
            "/captcha",
            //swagger
            "/v2/api-docs",
            "/swagger-resources/**",
            //德鲁伊监控
            "/druid/**"
    )));
    //@formatter:on
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        boolean anyMatch = ALLOWED_PATH.stream().anyMatch(s -> MATCHER.match(s, path));
        if (!anyMatch) {
            String servletPath = request.getServletPath();
            if (checkUrlExit(servletPath)) {
                List<String> permissionList = permissionList();
                if (CollectionUtils.isNotEmpty(permissionList) && permissionList.contains(servletPath)) {
                    return true;
                } else {
                    try {
                        log.error("没有权限");
                        OutputStream outputStream = response.getOutputStream();
                        response.setHeader("content-type", "text/html;charset=UTF-8");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        RestMessage restMessage = new RestMessage();
                        restMessage.setMessage("没有权限");
                        outputStream.write(JSON.toJSONString(restMessage).getBytes("utf-8"));
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 获取用户权限列表
     *
     * @return
     */
    private List<String> permissionList() {
        String userName = UserContext.getUser();
        SysUser user = sysUserService.getByUserName(userName);
        List<SysAuthority> sysAuthorities;
        if (CommonConstant.IS_ENABLE.equalsIgnoreCase(user.getIsAdmin())) {
            sysAuthorities = sysAuthorityService.listByEnable(YesOrNoEnum.YES.getCode());
        } else {
            sysAuthorities = sysUserService.listAuthorityByIdAndEnable(user.getId(), "Y");
        }
        return sysAuthorities.stream().map(SysAuthority::getUrl).filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
    }

    /**
     * 如果是返回true,表示需要认证。如果是返回false表示权限列表里没有,可以不需要认证
     *
     * @param perms
     *
     * @return
     */
    private boolean checkUrlExit(String perms) {
        boolean exit = false;
        List<SysAuthority> list = sysAuthorityService.listByEnable("");
        if (CollectionUtils.isNotEmpty(list)) {
            for (SysAuthority sysAuthority : list) {
                String validateUrl = sysAuthority.getUrl();
                if (StringUtils.isEmpty(validateUrl)) {
                    continue;
                }
                if (validateUrl.equalsIgnoreCase(perms)) {
                    exit = true;
                    break;
                }
            }
        }
        return exit;
    }
}
