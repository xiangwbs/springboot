package com.xwbing.starter.aspect;

import java.lang.reflect.Method;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.xwbing.starter.aspect.annotation.Limit;
import com.xwbing.starter.exception.ConfigException;
import com.xwbing.starter.redis.RedisService;

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
    private static final String LIMIT_KEY_PREFIX = "boot:limit_";
    @Resource
    private RedisService redisService;

    @Pointcut("@annotation(limit)")
    public void pointcut(Limit limit) {
    }

    /**
     * 目标方法执行时添加缓存
     *
     * @param joinPoint
     * @param limit
     */
    @Before(value = "pointcut(limit)", argNames = "joinPoint,limit")
    public void before(JoinPoint joinPoint, Limit limit) {
        String key = getKey(joinPoint, limit);
        String value = redisService.get(key);
        if (StringUtils.isEmpty(value)) {
            redisService.set(key, "limit", limit.timeout());
        } else {
            throw new ConfigException(limit.remark());
        }
    }

    /**
     * 目标方法执行后删除缓存
     *
     * @param joinPoint
     * @param limit
     */
    @AfterReturning(value = "pointcut(limit)", argNames = "joinPoint,limit")
    public void afterReturning(JoinPoint joinPoint, Limit limit) {
        String key = getKey(joinPoint, limit);
        redisService.del(key);
    }

    /**
     * 目标方法异常时删除缓存
     *
     * @param joinPoint
     * @param limit
     * @param exception
     */
    @AfterThrowing(value = "pointcut(limit)", throwing = "exception", argNames = "joinPoint,limit,exception")
    public void afterThrowing(JoinPoint joinPoint, Limit limit, Exception exception) {
        String key = getKey(joinPoint, limit);
        redisService.del(key);
        log.error("limitAspect key:{} error:", key, exception);
    }

    /**
     * 获取key
     * 获取注解value值 #p0 | #p0.getXxx() | #paramName
     *
     * @param joinPoint
     * @param limit
     *
     * @return
     */
    private String getKey(JoinPoint joinPoint, Limit limit) {
        String targetName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String key = limit.value();
        //创建SpEL表达式解析器
        ExpressionParser expressionParser = new SpelExpressionParser();
        //创建解析表达式上下文
        EvaluationContext context = new StandardEvaluationContext();
        //上下文中设置变量
        Object[] params = joinPoint.getArgs();
        for (int i = 0; i < params.length; i++) {
            //p0,p1......
            context.setVariable(String.format("%s%s", "p", i), params[i]);
        }
        Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        for (int i = 0; i < params.length; i++) {
            //paramName1,paraName2......
            context.setVariable(parameterNames[i], params[i]);
        }
        return LIMIT_KEY_PREFIX + targetName + "_" + methodName + "_" + String
                .valueOf(expressionParser.parseExpression(key).getValue(context));
    }
}