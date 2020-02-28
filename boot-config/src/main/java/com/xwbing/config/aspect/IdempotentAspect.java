package com.xwbing.config.aspect;

import com.xwbing.config.annotation.Idempotent;
import com.xwbing.config.util.CommonDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author xiangwb
 * 接口幂等切面
 * 解决接口幂等性 支持网络延迟和表单重复提交
 */
@Slf4j
@Aspect
public class IdempotentAspect {
    @Pointcut("@annotation(idempotent)")
    public void pointCut(Idempotent idempotent) {
    }

    @Around(value = "pointCut(idempotent)", argNames = "pjp,idempotent")
    public Object idempotent(ProceedingJoinPoint pjp, Idempotent idempotent) throws Throwable {
        String type = idempotent.type();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String sign;
        if ("header".equals(type)) {
            sign = request.getHeader("sign");
        } else {
            sign = request.getParameter("sign");
        }
        if (sign == null || sign.length() == 0) {
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
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = attributes.getResponse();
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            writer.println(msg);
        } catch (IOException e) {
            log.error("idempotent io error:{}", e.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
