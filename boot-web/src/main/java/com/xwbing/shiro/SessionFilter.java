package com.xwbing.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2018/2/3 15:05
 * 作者: xiangwb
 * 说明: session超时过滤
 */
public class SessionFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        // 只过滤了ajax请求时session超时
        if (httpServletRequest.getHeader("x-requested-with") != null && "XMLHttpRequest".equalsIgnoreCase(httpServletRequest.getHeader("x-requested-with"))) {
            Subject subject = SecurityUtils.getSubject();
            if (subject.getPrincipals() == null || subject.getPrincipals().getPrimaryPrincipal() == null || !subject.isAuthenticated()) {
                // 如果是ajax请求响应头会有，x-requested-with
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    out.append("登录超时，请重新登录!");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                return;
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
