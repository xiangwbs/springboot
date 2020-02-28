package com.xwbing.shiro;

import com.xwbing.domain.entity.sys.SysAuthority;
import com.xwbing.service.sys.SysAuthorityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2018/1/28 16:42
 * 作者: xiangwb
 * 说明: 自定义的URL认证
 */
@Component
public class UrlPermissionsFilter extends PermissionsAuthorizationFilter {
    @Resource
    private SysAuthorityService sysAuthorityService;

    /**
     * @param mappedValue 指的是在声明url时指定的权限字符串,我们要动态产生这个权限字符串，
     */
    @Override
    public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
        mappedValue = buildPermissions(request);
        Subject subject = getSubject(request, response);
        if (!subject.isAuthenticated()) {
            return false;
        }
        if (subject.getPrincipal() == null) {
            return false;
        }
        String[] perms = (String[]) mappedValue;
        boolean isPermitted = true;
        if (perms != null && perms.length > 0) {
            if (perms.length == 1) {
                if (checkUrlExit(perms[0])) {// 如果是需要认证的，继续，否则不继续
                    if (!subject.isPermitted(perms[0])) {
                        isPermitted = false;
                    }
                }
            } else {
                if (!subject.isPermittedAll(perms)) {
                    isPermitted = false;
                }
            }
        }
        return isPermitted;
    }

    /**
     * 如果是返回true,表示需要认证。如果是返回false表示权限列表里没有,可以不需要认证
     *
     * @param perms
     * @return
     */
    private boolean checkUrlExit(String perms) {
        boolean exitTag = false;
        List<SysAuthority> list = sysAuthorityService.listByEnable("");
        if (CollectionUtils.isNotEmpty(list)) {
            for (SysAuthority sysAuthority : list) {
                String validateUrl = sysAuthority.getUrl();
                if (StringUtils.isEmpty(validateUrl)) {
                    continue;
                }
                if (validateUrl.equalsIgnoreCase(perms)) {
                    exitTag = true;
                    break;
                }
            }
        }
        return exitTag;
    }

    /**
     * 根据请求URL产生权限字符串，这里只产生，而比对的事交给Realm
     *
     * @param request
     * @return
     */
    private String[] buildPermissions(ServletRequest request) {
        String[] perms = new String[1];
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getServletPath().substring(1);
        perms[0] = path;// path直接作为权限字符串
        return perms;
    }
}
