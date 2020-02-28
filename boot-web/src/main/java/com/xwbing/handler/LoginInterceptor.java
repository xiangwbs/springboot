package com.xwbing.handler;

import com.alibaba.fastjson.JSON;
import com.xwbing.util.CommonDataUtil;
import com.xwbing.util.HeaderUtil;
import com.xwbing.util.RestMessage;
import com.xwbing.util.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * 说明:  登录拦截器
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Slf4j
public class LoginInterceptor extends HandlerInterceptorAdapter {
    private static final Set<String> WHITE_LIST = new HashSet<>();//拦截器白名单

    static {
        //映射swagger文档
        WHITE_LIST.add("/doc");
        //验证码
        WHITE_LIST.add("/captcha");
        //swagger
        WHITE_LIST.add("/v2/api-docs");
        WHITE_LIST.add("/swagger-resources");
        WHITE_LIST.add("/configuration/ui");
        WHITE_LIST.add("/configuration/security");
        //德鲁伊监控
        WHITE_LIST.add("/druid");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String servletPath = request.getServletPath();
        if (!WHITE_LIST.contains(servletPath) && !servletPath.contains("test")) {
            HttpSession session = request.getSession(false);
            String token = HeaderUtil.getToken(request);
            if (session == null) {
                getOutputStream(response, "登录超时,请重新登录");
                CommonDataUtil.clearData(token);
                return false;
            } else {
                if (StringUtils.isEmpty(token)) {
                    getOutputStream(response, "token不能为空");
                    return false;
                } else {
                    if (CommonDataUtil.getData(token) != null) {
                        ThreadLocalUtil.setToken(token);
                        return true;
                    } else {
                        getOutputStream(response, "请先登录");
                        //未登录，重定向到登录页面
//                response.sendRedirect("/login.html");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void getOutputStream(HttpServletResponse response, String msg) {
        try {
            log.error(msg);
            OutputStream outputStream = response.getOutputStream();
            RestMessage restMessage = new RestMessage();
            restMessage.setMessage(msg);
            outputStream.write(JSON.toJSONString(restMessage).getBytes("utf-8"));
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
