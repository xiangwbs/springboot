package com.xwbing.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
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
@WebFilter(filterName = "fileFilter", urlPatterns = "/file/*")
@Order(3)
public class FileFilter implements Filter {
    private static final Set<String> WHITE_LIST = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) {
        WHITE_LIST.add("localhost:8080");
        WHITE_LIST.add("127.0.0.1:8080");
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
}
