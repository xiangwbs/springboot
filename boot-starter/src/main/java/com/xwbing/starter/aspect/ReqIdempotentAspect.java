package com.xwbing.starter.aspect;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.xwbing.starter.aspect.annotation.ReqIdempotent;
import com.xwbing.starter.exception.ConfigException;
import com.xwbing.starter.util.CommonDataUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 接口幂等切面
 * 解决接口幂等性 支持网络延迟和表单重复提交
 *
 * @author daofeng
 * @version $Id$
 */
@Slf4j
@Aspect
public class ReqIdempotentAspect {
    @Pointcut("@annotation(reqIdempotent)")
    public void pointCut(ReqIdempotent reqIdempotent) {
    }

    @Around(value = "pointCut(reqIdempotent)", argNames = "pjp,reqIdempotent")
    public Object idempotent(ProceedingJoinPoint pjp, ReqIdempotent reqIdempotent) throws Throwable {
        String type = reqIdempotent.type();
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String sign;
        if ("header".equals(type)) {
            sign = request.getHeader("sign");
        } else if ("param".equals(type)) {
            sign = request.getParameter("sign");
        } else {
            throw new ConfigException("幂等类型错误");
        }
        if (StringUtils.isEmpty(sign)) {
            response("sign不能为空");
            return null;
        }
        Object cache = CommonDataUtil.getData(sign);
        if (cache != null) {
            CommonDataUtil.clearData(sign);
        } else {
            response("请勿重复提交");
            return null;
        }
        return pjp.proceed();
    }

    private void response(String msg) {
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attributes.getResponse();
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.println(msg);
        } catch (IOException e) {
            log.error("idempotent io error:{}", e.getMessage());
        }
    }
}