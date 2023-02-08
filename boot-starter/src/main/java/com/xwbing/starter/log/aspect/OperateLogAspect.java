package com.xwbing.starter.log.aspect;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.xwbing.starter.log.annotation.OperateLog;
import com.xwbing.starter.log.function.CustomFunctionFactory;
import com.xwbing.starter.util.UserContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年02月06日 5:28 PM
 */
@RequiredArgsConstructor
@Slf4j
@Aspect
public class OperateLogAspect {
    /**
     * 自定义函数正则 {function{#param}}|{{#param}}
     */
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");
    public static final String FUNCTION_START = "{";
    /**
     * SpEL表达式语法前缀
     */
    public static final String SPEL_KEY = "#";
    public static final String RESULT = "_result";
    public static final String ERR_MSG = "_errMsg";
    /**
     * SpEL表达式解析器
     */
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final CustomFunctionFactory customFunctionFactory;

    @Around("@annotation(operateLog)")
    public Object log(ProceedingJoinPoint pjp, OperateLog operateLog) throws Throwable {
        String operator = UserContext.getUser();
        LocalDateTime operateDate = LocalDateTime.now();
        String content = operateLog.content();
        String tag = operateLog.tag();
        Object result = null;
        Boolean status = true;
        String errorMsg = null;
        Throwable error = null;
        // 创建解析表达式上下文
        EvaluationContext context = getEvaluationContext(pjp);
        // 前置自定义函数解析
        content = processBefore(content, context);
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
            // 后置自定义函数解析
            content = processAfter(content, result, errorMsg, context);
            // 存储日志
            // EsOperateLog dto = EsOperateLog.builder()
            //         .operator(operator)
            //         .operateDate(operateDate)
            //         .tag(tag)
            //         .content(content)
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

    private String processBefore(String content, EvaluationContext context) {
        if (content.contains(FUNCTION_START)) {
            Matcher matcher = FUNCTION_PATTERN.matcher(content);
            StringBuffer parsedStr = new StringBuffer();
            while (matcher.find()) {
                String functionName = matcher.group(1);
                String functionParam = matcher.group(2);
                if (functionParam.contains(SPEL_KEY + RESULT) || functionParam.contains(SPEL_KEY + ERR_MSG)) {
                    continue;
                }
                String functionResult = getFunctionResult(functionName, functionParam, context);
                matcher.appendReplacement(parsedStr, functionResult);
            }
            // 将从匹配的最后字符到整个字符串最后之间的字符串，追加到parsedStr中
            matcher.appendTail(parsedStr);
            content = parsedStr.toString();
        }
        return content;
    }

    private String processAfter(String content, Object result, String errorMsg, EvaluationContext context) {
        context.setVariable(RESULT, result);
        context.setVariable(ERR_MSG, errorMsg);
        if (content.contains(FUNCTION_START)) {
            Matcher matcher = FUNCTION_PATTERN.matcher(content);
            StringBuffer parsedStr = new StringBuffer();
            while (matcher.find()) {
                String functionName = matcher.group(1);
                String functionParam = matcher.group(2);
                String functionResult = getFunctionResult(functionName, functionParam, context);
                matcher.appendReplacement(parsedStr, functionResult);
            }
            matcher.appendTail(parsedStr);
            content = parsedStr.toString();
        } else if (content.contains(SPEL_KEY)) {
            content = expressionParser.parseExpression(content).getValue(context, String.class);
        }
        return content;
    }

    private String getFunctionResult(String functionName, String functionParam, EvaluationContext context) {
        Object value = expressionParser.parseExpression(functionParam).getValue(context);
        String valueStr = value == null ? "" : value.toString();
        return StringUtils.isNotEmpty(functionName) ? customFunctionFactory.apply(functionName, value) : valueStr;
    }

    private EvaluationContext getEvaluationContext(ProceedingJoinPoint joinPoint) {
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
        return context;
    }
}