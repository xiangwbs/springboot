package com.xwbing.shiro;

import com.xwbing.domain.entity.sys.SysUser;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2018/2/3 18:02
 * 作者: xiangwb
 * 说明: 防止二次登陆的功能
 */
public class KickoutSessionControlFilter extends AccessControlFilter {
    private String kickoutUrl; // 踢出后到的地址
    private boolean kickoutAfter; // 踢出之前登录的/之后登录的用户 默认踢出之前登录的用户
    private int maxSession = 1; // 同一个帐号最大会话数 默认1
    private SessionManager sessionManager;
    private Cache<String, Deque<Serializable>> cache;

    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }

    public void setKickoutAfter(boolean kickoutAfter) {
        this.kickoutAfter = kickoutAfter;
    }

    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cache = cacheManager.getCache("shiro-kickout-session");
    }

    /**
     * 是否允许访问，返回true表示允许
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     * @throws Exception
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        return false;
    }

    /**
     * 表示访问拒绝时是否自己处理，如果返回true表示自己不处理且继续拦截器链执行，返回false表示自己已经处理了（比如重定向到另一个页面）。
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        Subject subject = getSubject(request, response);
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            // 如果没有登录，直接进行之后的流程
            return true;
        }
        Session session = subject.getSession();
        Object object = subject.getPrincipal();
        SysUser sysUser = (SysUser) object;
        Serializable sessionId = session.getId();
        // 初始化用户的队列放到缓存里
        Deque<Serializable> deque = cache.get(sysUser.getUserName());
        if (deque == null || deque.size() == 0) {
            deque = new LinkedList<>();
            cache.put(sysUser.getUserName(), deque);
        }
        // 如果队列里没有此sessionId，且用户没有被踢出；放入队列
        if (!deque.contains(sessionId) && session.getAttribute("kickout") == null) {
            deque.push(sessionId);

        }
        // 如果队列里的sessionId数超出最大会话数，开始踢人
        while (deque.size() > maxSession) {
            Serializable kickoutSessionId;
            if (kickoutAfter) { // 如果踢出后者
                kickoutSessionId = deque.removeFirst();
            } else { // 否则踢出前者
                kickoutSessionId = deque.removeLast();
            }
            try {
                Session kickoutSession = sessionManager.getSession(new DefaultSessionKey(kickoutSessionId));
                if (kickoutSession != null) {
                    // 设置会话的kickout属性表示踢出了
                    kickoutSession.setAttribute("kickout", true);
                }
            } catch (Exception e) {// ignore exception
            }
        }
        // 如果被踢出了，直接退出，重定向到踢出后的地址
        if (session.getAttribute("kickout") != null) {
            // 会话被踢出了
            try {
                subject.logout();
            } catch (Exception e) { // ignore
            }
            saveRequest(request);
            WebUtils.issueRedirect(request, response, kickoutUrl);
            return false;
        }
        return true;
    }
}
