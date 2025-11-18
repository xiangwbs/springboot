package com.xwbing.web.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Yanlg
 * @version $Id$
 * @since 八月 10, 2020 14:28
 */

@Slf4j
public class ResponseFilter implements Filter {
    @Value("${bot.file.url.replaceStr:https://10.40.70.174/fileServer}")
    private String replaceFileUrl;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // 检查Accept头是否为text/event-stream
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String acceptHeader = httpRequest.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("text/event-stream")) {
            chain.doFilter(request, response);
            return;
        }
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) response);
        chain.doFilter(request, responseWrapper);
        String contentType = response.getContentType();
        // 只处理文本类型的内容
        if (StringUtils.isNotEmpty(contentType) && contentType.contains("application/json")) {
            if (StringUtils.isNotEmpty(replaceFileUrl)) {
                byte[] responseArray = responseWrapper.getContentAsByteArray();
                String characterEncoding = response.getCharacterEncoding();
                if (characterEncoding == null) {
                    characterEncoding = "UTF-8";
                }
                String responseStr = new String(responseArray, characterEncoding);
                // 替换字符串
                String modifiedResponse = responseStr.replaceAll("http://10.40.70.174/fileServer", replaceFileUrl);
                // 写回响应
                byte[] resultByte = modifiedResponse.getBytes(characterEncoding);
                response.setCharacterEncoding(characterEncoding);
                response.setContentLength(resultByte.length);
                response.getOutputStream().write(resultByte);
            }
        }
        responseWrapper.copyBodyToResponse();
    }
}
