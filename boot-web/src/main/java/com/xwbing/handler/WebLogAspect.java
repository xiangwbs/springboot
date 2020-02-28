package com.xwbing.handler;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.annotation.LogInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

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
    @Pointcut("within(com.xwbing.controller..*) && @annotation(logInfo)")
    public void pointCutWithMsg(LogInfo logInfo) {
    }

    //名称限定表达式
    @Pointcut("execution(public * com.xwbing.service..*.*(..))")
    public void pointCutService() {
    }

    /**
     * 前置通知
     *
     * @param logInfo
     */
    @Before(value = "pointCutWithMsg(logInfo)", argNames = "joinPoint,logInfo")
    public void before(JoinPoint joinPoint, LogInfo logInfo) {
        startTime.set(System.currentTimeMillis());
        //注解信息
        String info = logInfo.value();
        //获取相应类名
        String targetName = joinPoint.getTarget().getClass().getName();
        //获取方法名
        String methodName = joinPoint.getSignature().getName();
        //获取参数
        String params = Arrays.stream(joinPoint.getArgs())
                .filter(param -> !(param instanceof HttpServletRequest || param instanceof HttpServletResponse))
                .map(JSONObject::toJSONString).collect(Collectors.joining(","));
        if (StringUtils.isNotEmpty(params)) {
            log.info("{}.{}: {} started params:{}", targetName, methodName, info, params);
        } else {
            log.info("{}.{}: {} started", targetName, methodName, info);
        }
    }

    /**
     * 后置通知
     *
     * @param logInfo
     */
    @AfterReturning(pointcut = "pointCutWithMsg(logInfo)", argNames = "joinPoint,logInfo")
    public void afterReturning(JoinPoint joinPoint, LogInfo logInfo) {
        long end = System.currentTimeMillis();
        long ms = end - startTime.get();
        startTime.remove();
        String info = logInfo.value();
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
     * @return
     */
//    @Around(value = "pointCutWithMsg(logInfo)", argNames = "pjp,logInfo")
    public Object around(ProceedingJoinPoint pjp, LogInfo logInfo) throws Throwable {
        String info = logInfo.value();// 获取注解信息
        log.info("{} start", info);//前置通知
        Object result = pjp.proceed();
        log.info("{} end", info);//后置通知
        return result;
    }

    /**
     * 获取切入点信息
     *
     * @param joinPoint
     * @throws ClassNotFoundException
     */
    private void getMsg(JoinPoint joinPoint) throws ClassNotFoundException {
        String targetName = joinPoint.getTarget().getClass().getSimpleName();// 类名
        String methodName = joinPoint.getSignature().getName();// 方法名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class[] parameterTypes = methodSignature.getParameterTypes();//方法参数类型
        String[] parameterNames = methodSignature.getParameterNames();//方法参数名
        Object[] arguments = joinPoint.getArgs();// 方法参数值
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        Method method = methodSignature.getMethod();
        String value = method.getAnnotation(LogInfo.class).value();
    }
}
