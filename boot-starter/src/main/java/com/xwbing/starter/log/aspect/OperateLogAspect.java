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
     * 自定义语法正则 {functionName{SpEL}}|{{SpEL}}
     */
    private static final Pattern FUNCTION_PATTERN = Pattern.compile("\\{\\s*(\\w*)\\s*\\{(.*?)}}");
    /**
     * 自定义语法前缀
     */
    private static final String FUNCTION_START = "{";
    /**
     * SpEL语法前缀
     */
    private static final String SPEL_START = "#";
    /**
     * 方法返回结果key
     */
    private static final String RESULT = "_result";
    /**
     * 方法错误信息key
     */
    private static final String ERR_MSG = "_errMsg";
    /**
     * 操作人key
     */
    private static final String OPERATOR = "_operator";
    /**
     * SpEL解析器
     */
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    /**
     * 自定义函数工厂
     */
    private final CustomFunctionFactory customFunctionFactory;

    @Around("@annotation(operateLog)")
    public Object log(ProceedingJoinPoint joinPoint, OperateLog operateLog) throws Throwable {
        String operator = UserContext.getUser();
        LocalDateTime operateDate = LocalDateTime.now();
        String tag = operateLog.tag();
        String content = operateLog.content();
        Object result = null;
        Boolean status = true;
        String errorMsg = null;
        Throwable error = null;
        // 获取解析表达式上下文
        EvaluationContext context = getEvaluationContext(joinPoint);
        // 前置解析
        content = processBefore(context, content);
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            error = e;
            errorMsg = e.getMessage();
            status = false;
        } finally {
            // 解决登录接口登录时无用户信息问题
            if (StringUtils.isEmpty(operator)) {
                operator = UserContext.getUser();
            }
            // 后置解析
            content = processAfter(context, operator, result, errorMsg, content);
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

    private String processBefore(EvaluationContext context, String content) {
        if (!content.contains(FUNCTION_START)) {
            return content;
        }
        Matcher matcher = FUNCTION_PATTERN.matcher(content);
        StringBuffer parsedContent = new StringBuffer();
        while (matcher.find()) {
            String functionName = matcher.group(1);
            String functionParam = matcher.group(2);
            // 执行结果和异常信息后置解析
            if (functionParam.contains(SPEL_START + RESULT) || functionParam.contains(SPEL_START + ERR_MSG)) {
                continue;
            }
            String functionResult = getFunctionResult(context, functionName, functionParam);
            matcher.appendReplacement(parsedContent, functionResult);
        }
        matcher.appendTail(parsedContent);
        return parsedContent.toString();
    }

    private String processAfter(EvaluationContext context, String operator, Object result, String errorMsg,
            String content) {
        context.setVariable(OPERATOR, operator);
        context.setVariable(RESULT, result);
        context.setVariable(ERR_MSG, errorMsg);
        if (content.contains(FUNCTION_START)) {
            Matcher matcher = FUNCTION_PATTERN.matcher(content);
            StringBuffer parsedStr = new StringBuffer();
            while (matcher.find()) {
                String functionName = matcher.group(1);
                String functionParam = matcher.group(2);
                // 防止方法执行报错时，获取执行结果属性时npe
                if (StringUtils.isNotEmpty(errorMsg) && functionParam.contains(SPEL_START + RESULT + ".")) {
                    matcher.appendReplacement(parsedStr, "");
                } else {
                    String functionResult = getFunctionResult(context, functionName, functionParam);
                    matcher.appendReplacement(parsedStr, functionResult);
                }
            }
            matcher.appendTail(parsedStr);
            content = parsedStr.toString();
        } else if (content.contains(SPEL_START)) {
            content = expressionParser.parseExpression(content).getValue(context, String.class);
        }
        return content;
    }

    private String getFunctionResult(EvaluationContext context, String functionName, String functionParam) {
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