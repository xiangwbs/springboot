package com.xwbing.config.aspect;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.aop.ThrowsAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author xiangwb
 * 微服务场景下
 * rpc调用中台服务异常日志记录,便于排查问题(GlobalExceptionHandler无法捕捉异常)
 */
@Slf4j
public class ExceptionLogAdvice implements ThrowsAdvice {
    /**
     * 异常通知
     *
     * @param method
     * @param args
     * @param target
     * @param exception
     * @throws Throwable
     */
    public void afterThrowing(Method method, Object[] args, Object target, Exception exception) throws Throwable {
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();
        String params = Arrays.stream(args)
                .filter(param -> !(param instanceof HttpServletRequest || param instanceof HttpServletResponse))
                .map(JSONObject::toJSONString).collect(Collectors.joining(","));
        String stackTrace = ExceptionUtils.getStackTrace(exception);
        if (StringUtils.isNotEmpty(params)) {
            log.error("{}.{} - params:{} - exception:{}", className, methodName, params, stackTrace);
        } else {
            log.error("{}.{} - exception:{}", className, methodName, stackTrace);
        }
        throw exception;
    }
}
