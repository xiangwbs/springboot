package com.xwbing.web.handler;

import com.xwbing.web.annotation.NoLoginRequired;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xiangwb
 * @date 2019/1/24 19:20
 * 防盗链过滤器
 */
// @WebFilter(filterName = "fileFilter", urlPatterns = "/file/*")
@Slf4j
@Order(3)
public class FileFilter implements Filter {
    private static final Set<String> WHITE_LIST = new HashSet<>();
    private RequestMappingHandlerMapping handlerMapping;


    @Override
    public void init(FilterConfig filterConfig) {
        WHITE_LIST.add("localhost:8080");
        WHITE_LIST.add("127.0.0.1:8080");
        ServletContext sc = filterConfig.getServletContext();
        WebApplicationContext cxt = WebApplicationContextUtils.getWebApplicationContext(sc);
        handlerMapping = cxt.getBean(RequestMappingHandlerMapping.class);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String referer = request.getHeader("Referer");
        if (StringUtils.isEmpty(referer)) {
            request.getRequestDispatcher("/file/error.png").forward(request, response);
            return;
        }
        String domain = getDomain(referer);
        if (!WHITE_LIST.contains(domain)) {
            request.getRequestDispatcher("/file/error.png").forward(request, response);
            return;
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    private String getDomain(String url) {
        int index = 0, startIndex = 0, endIndex = 0;
        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) == '/') {
                index++;
                if (index == 2) {
                    startIndex = i;
                    continue;
                }
                if (index == 3) {
                    endIndex = i;
                    break;
                }
            }
        }
        try {
            return url.substring(startIndex + 1, endIndex);
        } catch (StringIndexOutOfBoundsException e) {
            return "";
        }
    }

    public Boolean isNoLogin(HttpServletRequest request) {
        try {
            HandlerExecutionChain executionChain = handlerMapping.getHandler(request);
            if (executionChain != null) {
                Object handler = executionChain.getHandler();
                if (handler instanceof HandlerMethod) {
                    HandlerMethod handlerMethod = (HandlerMethod) handler;
                    return handlerMethod.hasMethodAnnotation(NoLoginRequired.class);
                }
            }
        } catch (Exception e) {
            log.error("handlerMapping.getHandler error", e);
        }
        return false;
    }
}
