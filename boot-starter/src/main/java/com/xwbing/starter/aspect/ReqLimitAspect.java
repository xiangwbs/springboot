package com.xwbing.starter.aspect;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
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
    private static final String LIMIT_KEY_PREFIX = "boot:reqLimit_%s_%s_%s";
    /**
     * SpEL表达式解析器
     */
    private final ExpressionParser expressionParser = new SpelExpressionParser();
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
     * 获取注解value值 #p0 | #p0.field | #paramName | #paramName.field
     *
     * @param joinPoint
     * @param reqLimit
     *
     * @return
     */
    private String getKey(JoinPoint joinPoint, ReqLimit reqLimit) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        String methodName = methodSignature.getName();
        List<String> paramNameList = Arrays.asList(methodSignature.getParameterNames());
        List<Object> paramValueList = Arrays.asList(joinPoint.getArgs());
        // 创建解析表达式上下文
        EvaluationContext context = new StandardEvaluationContext();
        // 上下文中设置变量
        for (int i = 0; i < paramNameList.size(); i++) {
            // paramName1,paraName2......
            context.setVariable(paramNameList.get(i), paramValueList.get(i));
            // p0,p1......
            context.setVariable(String.format("p%s", i), paramValueList.get(i));
        }
        return String.format(LIMIT_KEY_PREFIX, className, methodName,
                expressionParser.parseExpression(reqLimit.value()).getValue(context, String.class));
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