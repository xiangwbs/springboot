package com.xwbing.handler;

import com.alibaba.fastjson.JSON;
import com.xwbing.constant.CommonConstant;
import com.xwbing.constant.CommonEnum;
import com.xwbing.domain.entity.sys.SysAuthority;
import com.xwbing.domain.entity.sys.SysUser;
import com.xwbing.service.sys.SysAuthorityService;
import com.xwbing.service.sys.SysUserService;
import com.xwbing.util.CommonDataUtil;
import com.xwbing.util.RestMessage;
import com.xwbing.util.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class UrlPermissionsInterceptor extends HandlerInterceptorAdapter {
    @Resource
    private SysAuthorityService sysAuthorityService;
    @Resource
    private SysUserService sysUserService;
    private static final Set<String> SET = new HashSet<>();//拦截器白名单

    static {
        //映射swagger文档
        SET.add("doc");
        //验证码
        SET.add("captcha");
        //swagger
        SET.add("v2/api-docs");
        SET.add("swagger-resources");
        SET.add("configuration/ui");
        SET.add("configuration/security");
        //德鲁伊监控
        SET.add("druid");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String servletPath = request.getServletPath().substring(1);
        if (!SET.contains(servletPath) && !servletPath.contains("test")) {
            if (checkUrlExit(servletPath)) {
                List<String> permissionList = permissionList();
                if (CollectionUtils.isNotEmpty(permissionList) && permissionList.contains(servletPath)) {
                    return true;
                } else {
                    try {
                        log.error("没有权限");
                        OutputStream outputStream = response.getOutputStream();
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
        String token = ThreadLocalUtil.getToken();
        String userName = (String) CommonDataUtil.getData(token);
        SysUser user = sysUserService.getByUserName(userName);
        List<SysAuthority> sysAuthorities;
        if (CommonConstant.IS_ENABLE.equalsIgnoreCase(user.getIsAdmin())) {
            sysAuthorities = sysAuthorityService.listByEnable(CommonEnum.YesOrNoEnum.YES.getCode());
        } else {
            sysAuthorities = sysUserService.listAuthorityByIdAndEnable(user.getId(), "Y");
        }
        return sysAuthorities.stream().map(SysAuthority::getUrl).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
    }

    /**
     * 如果是返回true,表示需要认证。如果是返回false表示权限列表里没有,可以不需要认证
     *
     * @param perms
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
