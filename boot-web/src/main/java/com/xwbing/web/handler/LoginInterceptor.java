package com.xwbing.web.handler;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.util.HeaderUtil;
import com.xwbing.service.util.RestMessage;
import com.xwbing.service.util.ThreadLocalUtil;
import com.xwbing.starter.util.CommonDataUtil;
import com.xwbing.starter.util.UserContext;
import com.xwbing.web.annotation.NoLoginRequired;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 说明:  登录拦截器
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 16:36
 *
 * @author xwbing
 */
@Slf4j
public class LoginInterceptor extends HandlerInterceptorAdapter {
    private static final AntPathMatcher MATCHER = new AntPathMatcher();
    //@formatter:off
    private static final Set<String> ALLOWED_PATH = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            //映射swagger文档
            "/doc",
            //验证码
            "/captcha",
            //swagger
            "/swagger-ui.html",
            "/v2/api-docs",
            "/swagger-resources/**",
            "/webjars/**",
            //德鲁伊监控
            "/druid/**"
    )));
    //@formatter:on
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String traceId = request.getHeader("traceId");
        if (traceId == null) {
            traceId = IdUtil.simpleUUID();
        }
        ThreadLocalUtil.setTraceId(traceId);
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            boolean noLogin = method.hasMethodAnnotation(NoLoginRequired.class);
            if (noLogin) {
                return true;
            }
        }
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        boolean anyMatch = ALLOWED_PATH.stream().anyMatch(s -> MATCHER.match(s, path));
        if (!anyMatch) {
            String token = HeaderUtil.getToken(request);
            if (StringUtils.isEmpty(token)) {
                getOutputStream(response, "请先登录");
                return false;
            } else {
                Object user = CommonDataUtil.getData(token);
                if (user != null) {
                    UserContext.setUser(String.valueOf(user));
                    ThreadLocalUtil.setToken(token);
                    return true;
                } else {
                    getOutputStream(response, "登录超时，请重新登录");
                    //未登录，重定向到登录页面
                    //response.sendRedirect(request.getContextPath()+"/login");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        UserContext.clearUser();
        ThreadLocalUtil.clearTraceId();
        ThreadLocalUtil.clearToken();
    }

    private void getOutputStream(HttpServletResponse response, String msg) {
        try {
            log.error(msg);
            OutputStream outputStream = response.getOutputStream();
            response.setHeader("content-type", "text/html;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            RestMessage restMessage = new RestMessage();
            restMessage.setMessage(msg);
            outputStream.write(JSONObject.toJSONString(restMessage).getBytes("utf-8"));
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
