package com.xwbing.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author xiangwb
 * @date 2019/1/24 19:20
 * 防盗链过滤器
 */
@WebFilter(filterName = "fileFilter", urlPatterns = "/file/*")
@Order(3)
public class FileFilter implements Filter {
    @Value("localhost:8080")
    private String domainName;

    @Override
    public void init(FilterConfig filterConfig) {
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
        if (!domain.equals(domainName)) {
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
