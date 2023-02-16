package com.xwbing.web.handler;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年10月07日 8:56 PM
 */
// @WebFilter(filterName = "corsFilter", urlPatterns = "/*")
@Order(1)
public class CorsFilter implements Filter {
    private static final String[] ALLOW_HEADERS = { HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT, "token" };
    private static final String[] ALLOW_METHODS = { HttpMethod.GET.name(), HttpMethod.POST.name(),
            HttpMethod.PUT.name(), HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name() };
    private static final String[] EXPOSE_HEADERS = { HttpHeaders.CONTENT_DISPOSITION, "token" };

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse)res;
        HttpServletRequest request = (HttpServletRequest)req;
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, Boolean.TRUE.toString());
        response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, String.valueOf((60 * 60 * 24)));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(HttpHeaders.ORIGIN));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, String.join(",", ALLOW_METHODS));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, String.join(",", ALLOW_HEADERS));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, String.join(",", EXPOSE_HEADERS));
        if (HttpMethod.OPTIONS.toString().equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpStatus.OK.value());
        } else {
            chain.doFilter(req, res);
        }
    }
}
