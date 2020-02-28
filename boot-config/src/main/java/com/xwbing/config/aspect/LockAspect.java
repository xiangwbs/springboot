package com.xwbing.config.aspect;

import com.xwbing.config.annotation.Lock;
import com.xwbing.config.exception.LockException;
import com.xwbing.config.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author xiangwb
 * 分布式锁切面
 */
@Slf4j
@Aspect
public class LockAspect {
    private static final String SUFFIX = ".lock";
    @Resource
    private RedisService redisService;

    @Pointcut("@annotation(lock)")
    public void lockCut(Lock lock) {
    }

    @Around(value = "lockCut(lock)", argNames = "joinPoint,lock")
    public Object around(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String subject = "";
        String value = lock.value();
        if (StringUtils.isNotEmpty(value)) {
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
            ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
            for (int i = 0; i < params.length; i++) {
                //paramName1,paraName2......
                context.setVariable(parameterNames[i], params[i]);
            }
            //获取上下文变量值 #p0 | #p0.getXxx() | #paramName | 基本数据类型
            subject = String.valueOf(expressionParser.parseExpression(value).getValue(context));
        }
        String operator = StringUtils.isNotEmpty(lock.operator()) ? lock.operator()
                : String.format("%s.%s", joinPoint.getTarget().getClass().getName(), method.getName());
        try {
            log.info("try lock");
            this.lock(subject, operator, lock.timeout(), lock.remark());
            return joinPoint.proceed();
        } finally {
            this.unlock(subject, operator);
        }
    }

    public void lock(String key, int timeout, String remark) throws LockException {
        log.debug("request lock , the key is : {}", key);
        key = String.format("%s%s", key, SUFFIX);
        Long now = System.currentTimeMillis();
        try {
            Long lock = redisService.setNx(key, String.valueOf(now));
            if (!(lock != null && lock.equals(1L))) {
                //如果已经加过锁,则判断加锁是否已经失效
                String current = redisService.get(key);
                now = System.currentTimeMillis();
                if (current != null && now - Long.parseLong(current) > timeout) {
                    //如果加锁已经失效,则加锁
                    String old = redisService.getSet(key, String.valueOf(now));
                    //判断上一次的锁是否就是失效的锁
                    if (old != null && old.equals(current)) {
                        //如果是则加锁成功
                        return;
                    }  //如果不是则加锁异常
                }  //如果未失效则返回异常
                throw new LockException(remark);
            }
        } finally {
            //统一设置过期时间，防止发生死锁
            redisService.expire(key, timeout);
        }
    }

    public void lock(String operator, String subject, int timeout, String remark) throws LockException {
        this.lock(String.format("%s.%s", operator, subject), timeout, remark);
    }

    public void unlock(String key) {
        log.debug("request unlock , the key is : {}", key);
        key = String.format("%s%s", key, SUFFIX);
        redisService.del(key);
    }

    public void unlock(String operator, String subject) {
        this.unlock(String.format("%s.%s", operator, subject));
    }
}
