package com.xwbing.config.aspect;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import com.xwbing.config.annotation.Limit;
import com.xwbing.config.exception.ConfigException;
import com.xwbing.config.redis.RedisService;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作频率限制切面
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年08月03日 下午5:36
 */
@Slf4j
@Aspect
public class LimitAspect {
    private static final String SUFFIX = "/limit";
    @Resource
    private RedisService redisService;

    @Pointcut("@annotation(limit)")
    public void pointcut(Limit limit) {
    }

    @SneakyThrows
    @Before(value = "pointcut(limit)", argNames = "joinPoint,limit")
    public void before(JoinPoint joinPoint, Limit limit) {
        String key = getKey(joinPoint);
        int timeout = limit.timeout();
        String value = redisService.get(key);
        if (StringUtils.isEmpty(value)) {
            redisService.set(key, "limit", timeout);
        } else {
            throw new ConfigException("您的操作太快, 请稍后再试");
        }
    }

    @SneakyThrows
    @AfterReturning(pointcut = "pointcut(com.xwbing.config.annotation.Limit)")
    public void afterReturning(JoinPoint joinPoint) {
        String key = getKey(joinPoint);
        redisService.del(key);
    }

    private String getKey(JoinPoint joinPoint) throws UnknownHostException {
        String targetName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String ip = InetAddress.getLocalHost().getHostAddress();
        return targetName + "/" + methodName + "/" + ip + SUFFIX;
    }
}
