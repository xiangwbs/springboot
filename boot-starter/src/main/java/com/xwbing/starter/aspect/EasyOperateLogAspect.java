package com.xwbing.starter.aspect;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.xwbing.starter.aspect.annotation.EasyOperateLog;
import com.xwbing.starter.util.UserContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年02月06日 5:28 PM
 */
@Slf4j
@Aspect
public class EasyOperateLogAspect {
    /**
     * SpEL表达式解析器
     */
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    @Around("@annotation(operateLog)")
    public Object log(ProceedingJoinPoint pjp, EasyOperateLog operateLog) throws Throwable {
        String operator = UserContext.getUser();
        LocalDateTime operateDate = LocalDateTime.now();
        String tag = operateLog.tag();
        Object[] args = pjp.getArgs();
        String content = parseContent(pjp, operateLog.content());
        Object result = null;
        Boolean status = true;
        String errorMsg = null;
        Throwable error = null;
        try {
            result = pjp.proceed();
        } catch (Throwable e) {
            error = e;
            errorMsg = e.getMessage();
            status = false;
        } finally {
            // 解决登录接口登录时无用户信息问题
            if (StringUtils.isEmpty(operator)) {
                operator = UserContext.getUser();
            }
            // 存储日志
            // EsOperateLog dto = EsOperateLog.builder()
            //         .operator(operator)
            //         .operateDate(operateDate)
            //         .tag(tag)
            //         .content(content)
            //         .args(JSONUtil.toJsonStr(args))
            //         .result(operateLog.saveResult() ? JSONUtil.toJsonPrettyStr(result) : null)
            //         .status(status)
            //         .errorMsg(errorMsg)
            //         .build();
            if (error != null) {
                log.error("operateLog error", error);
                throw error;
            }
        }
        return result;
    }

    private String parseContent(ProceedingJoinPoint joinPoint, String content) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
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
        return expressionParser.parseExpression(content).getValue(context, String.class);
    }
}