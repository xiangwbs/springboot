package com.xwbing.handler;

import com.xwbing.annotation.LogInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    @Pointcut("execution(public * com.xwbing.controller..*.*(..))")
    public void pointCut() {
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
        Object[] args = joinPoint.getArgs();
        List<Object> params = new ArrayList<>();
        if (args != null && args.length > 0) {
            params = Arrays.stream(args).filter(o -> !(o instanceof HttpServletRequest || o instanceof HttpServletResponse)).collect(Collectors.toList());
        }
        log.info("{}/{}:{} started 参数:{}", targetName, methodName, info, params);
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
        log.info("{}/{}:{} completed in {} ms", targetName, methodName, info, ms);
    }

    /**
     * 异常通知
     *
     * @param e
     */
//    @AfterThrowing(pointcut = "pointCut()", throwing = "e")
    public void afterThroing(Exception e) {
        log.error("异常信息:{}", e.getMessage());
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
     * 获取日志注解信息
     *
     * @param joinPoint
     * @return
     * @throws ClassNotFoundException
     */
    @Deprecated
    public String getMsg(JoinPoint joinPoint) throws ClassNotFoundException {
        String targetName = joinPoint.getTarget().getClass().getName();// 类名
        String methodName = joinPoint.getSignature().getName();// 方法名
        Object[] arguments = joinPoint.getArgs();// 方法参数
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] classes = method.getParameterTypes();
                if (classes.length == arguments.length) {
                    description = method.getAnnotation(LogInfo.class).value();
                    break;
                }
            }
        }
        return description;
    }
}
