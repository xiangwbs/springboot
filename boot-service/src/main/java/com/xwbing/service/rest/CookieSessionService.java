package com.xwbing.service.rest;

import com.xwbing.util.RestMessage;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建时间: 2017/12/19 10:33
 * 作者: xiangwb
 * 说明: cookie session服务层
 */
@Service
public class CookieSessionService {

    public RestMessage session(HttpServletRequest request) {
        RestMessage restMessage = new RestMessage();
//        WebUtils.setSessionAttribute(request, "session", "sessionValue");
//        String sessionValue = (String) WebUtils.getSessionAttribute(request, "sessionValue");
        HttpSession session = request.getSession();
        session.setAttribute("session", "sessionValue");
        String sessionId = session.getId();
        session.setMaxInactiveInterval(60 * 60);//单位为秒,-1永不过期
        String test = (String) session.getAttribute("session");
        restMessage.setData("sessionId:" + sessionId + ",value:" + test);
        restMessage.setSuccess(true);
        return restMessage;
    }

    public RestMessage cookie(HttpServletResponse response, HttpServletRequest request) {
        RestMessage restMessage = new RestMessage();
        Cookie cookie = new Cookie("sb", "xwjun");
        cookie.setMaxAge(60 * 60);//单位为秒
        //设置路径,这个路径即该工程下都可以访问该cookie 如果不设置路径,那么只有设置该cookie路径及其子路径可以访问
        cookie.setPath("/");
        response.addCookie(cookie);
        Map<String, Cookie> cookieMap = readCookieMap(request);
        restMessage.setData(cookieMap);
        restMessage.setSuccess(true);
        return restMessage;
    }

    /**
     * 将cookie封装到Map里面
     *
     * @param request
     * @return
     */
    private Map<String, Cookie> readCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();//这样便可以获取一个cookie数组
        if (null != cookies) {//JSESSIONID:java sessionId
            Arrays.stream(cookies).forEach(cookie -> cookieMap.put(cookie.getName(), cookie));
        }
        return cookieMap;
    }
}
