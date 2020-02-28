package com.xwbing.config.aspect;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.config.annotation.OptimisticLockRetry;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.StaleObjectStateException;
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;
import org.springframework.transaction.HeuristicCompletionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author xiangwb
 * 乐观锁异常切面
 */
@Slf4j
@Aspect
public class OptimisticLockRetryAspect {
    @Pointcut("@annotation(optimisticLockRetry)")
    public void retryCut(OptimisticLockRetry optimisticLockRetry) {
    }

    @Around(value = "retryCut(optimisticLockRetry)", argNames = "joinPoint,optimisticLockRetry")
    public Object around(ProceedingJoinPoint joinPoint, OptimisticLockRetry optimisticLockRetry) throws Throwable {
        Exception optimisticLockException;
        String params = null;
        int tries = 0;
        do {
            try {
                return joinPoint.proceed();
            } catch (Exception exception) {
                if (exception instanceof HeuristicCompletionException
                        || exception instanceof HibernateOptimisticLockingFailureException
                        || exception instanceof StaleObjectStateException) {
                    tries++;
                    optimisticLockException = exception;
                    String className = joinPoint.getTarget().getClass().getSimpleName();
                    String methodName = joinPoint.getSignature().getName();
                    params = params != null ? params : Arrays.stream(joinPoint.getArgs())
                            .filter(param -> !(param instanceof HttpServletRequest || param instanceof HttpServletResponse))
                            .map(JSONObject::toJSONString).collect(Collectors.joining(","));
                    log.info("class:{} method:{} params:{}", className, methodName, params);
                } else {
                    throw exception;
                }
            }
        } while (tries <= optimisticLockRetry.value());
        throw optimisticLockException;
    }
}
