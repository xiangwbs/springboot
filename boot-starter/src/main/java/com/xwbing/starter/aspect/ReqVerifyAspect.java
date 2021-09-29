package com.xwbing.starter.aspect;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.xwbing.starter.aspect.annotation.ReqVerify;
import com.xwbing.starter.aspect.enums.ReqVerifyBizTypeEnum;
import com.xwbing.starter.exception.ConfigException;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 验签切面
 * 签名明文规则:bizType=xxx&timestamp=xxx&xxx=xxx
 *
 * @author daofeng
 * @version $Id$
 * @since 2021年07月15日 1:54 PM
 */
@Slf4j
@Aspect
@AllArgsConstructor
public class ReqVerifyAspect {
    private final RSA rsa;
    private static final String ERROR = "请求不合法";
    private static final String BIZ_TYPE = "bizType";
    private static final String TIMESTAMP = "timestamp";

    @Pointcut("(@within(org.springframework.stereotype.Controller)||@within(org.springframework.web.bind.annotation.RestController))&&(@within(reqVerify) || @annotation(reqVerify))")
    public void pointcut(ReqVerify reqVerify) {
    }

    @Before(value = "pointcut(reqVerify)", argNames = "joinPoint,reqVerify")
    public void before(JoinPoint joinPoint, ReqVerify reqVerify) {
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameters =  methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> map = new HashMap<>(parameterMap.size());
        parameterMap.forEach((k, v) -> {
            if (ArrayUtil.length(v) == 1) {
                map.put(k, v[0]);
            }
        });
        // 获取明文参数
        String sign = request.getHeader("sign");
        if (StringUtils.isEmpty(sign)) {
            log.error("reqVerifyAspect no sign");
            throw new ConfigException(ERROR);
        }
        String decryptStr;
        try {
            decryptStr = rsa.decryptStr(sign, KeyType.PrivateKey);
        } catch (Exception e) {
            log.error("reqVerifyAspect decrypt error", e);
            throw new ConfigException(ERROR);
        }

        Map<String, String> paramMap = Arrays.stream(decryptStr.split("&")).collect(Collectors
                .toMap(str -> str.split("=")[0], str -> str.split("=").length >= 2 ? str.split("=")[1] : null));
        // 业务校验
        ReqVerifyBizTypeEnum bizType = ReqVerifyBizTypeEnum.parse(paramMap.get(BIZ_TYPE));
        if (bizType == null) {
            throw new ConfigException(ERROR);
        }
        switch (bizType) {
            //bizType=default&timestamp=xxx
            case DEFAULT:
                break;
            default:
                break;
        }
        // 过期时间校验 1分钟失效
        String timestamp = paramMap.get(TIMESTAMP);
        if (StringUtils.isEmpty(timestamp) || timestampToLocalDateTime(timestamp).plusMinutes(1)
                .isBefore(LocalDateTime.now())) {
            throw new ConfigException(ERROR);
        }
    }

    private LocalDateTime timestampToLocalDateTime(String ms) {
        Instant instant = Instant.ofEpochMilli(Long.valueOf(ms));
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}