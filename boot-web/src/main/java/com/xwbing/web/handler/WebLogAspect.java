package com.xwbing.web.handler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.web.annotation.LogInfo;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 说明: web层日志记录切面
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 16:34
 * 作者:  xiangwb
 */
@Slf4j
@Aspect
@Component
public class WebLogAspect {
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    //类型限定表达式
    @Pointcut("within(com.xwbing.web.controller..*) && @annotation(operation)")
    public void pointCutWithMsg(ApiOperation operation) {
    }

    //名称限定表达式
    @Pointcut("execution(public * com.xwbing.service.service..*.*(..))")
    public void pointCutService() {
    }

    /**
     * 前置通知
     *
     * @param operation
     */
    @Before(value = "pointCutWithMsg(operation)", argNames = "joinPoint,operation")
    public void before(JoinPoint joinPoint, ApiOperation operation) {
        startTime.set(System.currentTimeMillis());
        //注解信息
        String info = operation.value();
        //获取相应类名
        String targetName = joinPoint.getTarget().getClass().getName();
        //获取方法名
        String methodName = joinPoint.getSignature().getName();
        //获取参数
        String params = Arrays.stream(joinPoint.getArgs())
                .filter(param -> !(param instanceof HttpServletRequest || param instanceof HttpServletResponse
                        || param instanceof MultipartFile)).map(JSONObject::toJSONString)
                .collect(Collectors.joining(","));
        if (StringUtils.isNotEmpty(params)) {
            log.info("{}.{}: {} started params:{}", targetName, methodName, info, params);
        } else {
            log.info("{}.{}: {} started", targetName, methodName, info);
        }
    }

    /**
     * 后置通知
     *
     * @param operation
     */
    @AfterReturning(pointcut = "pointCutWithMsg(operation)", argNames = "joinPoint,operation")
    public void afterReturning(JoinPoint joinPoint, ApiOperation operation) {
        long end = System.currentTimeMillis();
        long ms = end - startTime.get();
        startTime.remove();
        String info = operation.value();
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        log.info("{}.{}: {} completed in {} ms", targetName, methodName, info, ms);
    }

    /**
     * 异常通知
     * 适用于rpc远程调用中台服务异常记录(GlobalExceptionHandler无法捕捉异常)
     *
     * @param joinPoint
     * @param exception
     */
    //    @AfterThrowing(pointcut = "pointCutService()", throwing = "exception")
    public void afterThrowing(JoinPoint joinPoint, Exception exception) {
        String stackTrace = ExceptionUtils.getStackTrace(exception);
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String params = Arrays.stream(joinPoint.getArgs())
                .filter(param -> !(param instanceof HttpServletRequest || param instanceof HttpServletResponse))
                .map(JSONObject::toJSONString).collect(Collectors.joining(","));
        if (StringUtils.isNotEmpty(params)) {
            log.error("{}.{} - params:{} - exception:{}", className, methodName, params, stackTrace);
        } else {
            log.error("{}.{} - exception:{}", className, methodName, stackTrace);
        }
    }

    /**
     * 环绕通知=前置+后置
     * 如果return null有拦截的效果
     *
     * @param pjp
     * @param logInfo
     *
     * @return
     */
    // @Around(value = "pointCutWithMsg(apiOperation)", argNames = "pjp,apiOperation")
    public Object around(ProceedingJoinPoint pjp, LogInfo logInfo) {
        //前置通知
        long start = System.currentTimeMillis();
        String type = logInfo.type();
        Object[] args = pjp.getArgs();
        Object result = null;
        Boolean status = true;
        String errorMsg=null;
        Throwable error=null;
        try {
            result = pjp.proceed();
        } catch (Throwable e) {
            error = e;
            errorMsg = e.toString();
            status = false;
        }finally {
            //后置通知
            long end = System.currentTimeMillis();
            long cost = end-start;
            //TODO 记录日志信息到数据库 type,args,result,status,start,end,cost,errorMsg
            if (error!=null) {
                log.error("...",error);
                throw new RuntimeException(errorMsg);
            }
        }
        return result;
    }

    /**
     * 获取切入点信息
     *
     * @param joinPoint
     *
     * @throws ClassNotFoundException
     */
    private void getMsg(JoinPoint joinPoint) throws ClassNotFoundException {
        String targetName = joinPoint.getTarget().getClass().getSimpleName();// 类名
        String methodName = joinPoint.getSignature().getName();// 方法名
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Class[] parameterTypes = methodSignature.getParameterTypes();//方法参数类型
        String[] parameterNames = methodSignature.getParameterNames();//方法参数名
        Object[] arguments = joinPoint.getArgs();// 方法参数值
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        Method method = methodSignature.getMethod();
        String type = method.getAnnotation(LogInfo.class).type();
    }

    private String getRequestUri() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        return attributes.getRequest().getRequestURI();
    }
}
