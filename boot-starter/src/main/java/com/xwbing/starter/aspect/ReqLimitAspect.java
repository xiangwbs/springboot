package com.xwbing.starter.aspect;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
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

import com.xwbing.starter.aspect.annotation.ReqLimit;
import com.xwbing.starter.exception.ConfigException;
import com.xwbing.starter.redis.RedisService;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ReqLimitAspect {
    private static final String LIMIT_KEY_PREFIX = "boot:reqLimit_";
    private final RedisService redisService;

    @Pointcut("@annotation(reqLimit)")
    public void pointcut(ReqLimit reqLimit) {
    }

    /**
     * 目标方法执行时添加缓存
     *
     * @param joinPoint
     * @param reqLimit
     */
    @Before(value = "pointcut(reqLimit)", argNames = "joinPoint,reqLimit")
    public void before(JoinPoint joinPoint, ReqLimit reqLimit) {
        String key = getKey(joinPoint, reqLimit);
        String value = redisService.get(key);
        if (StringUtils.isEmpty(value)) {
            redisService.set(key, "limit", reqLimit.timeout());
        } else {
            throw new ConfigException(reqLimit.remark());
        }
    }

    /**
     * 目标方法异常时删除缓存
     *
     * @param joinPoint
     * @param reqLimit
     * @param exception
     */
    @AfterThrowing(value = "pointcut(reqLimit)", throwing = "exception", argNames = "joinPoint,reqLimit,exception")
    public void afterThrowing(JoinPoint joinPoint, ReqLimit reqLimit, Exception exception) {
        String key = getKey(joinPoint, reqLimit);
        redisService.del(key);
        log.error("limitAspect key:{} error:", key, exception);
    }

    /**
     * 获取key
     * 获取注解value值 #p0 | #p0.getXxx() | #paramName
     *
     * @param joinPoint
     * @param reqLimit
     *
     * @return
     */
    private String getKey(JoinPoint joinPoint, ReqLimit reqLimit) {
        String targetName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String key = reqLimit.value();
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

    // public static void main(String[] args) {
    //     Long limit = redisService.incrBy("", 1);
    //     if (limit > 1) {
    //         throw new RuntimeException("");
    //     }
    //     redisService.expire("", 10);
    //     try {
    //         // TODO:
    //     } catch (Exception e) {
    //         // TODO:
    //     } finally {
    //         redisService.del("");
    //     }
    // }
}