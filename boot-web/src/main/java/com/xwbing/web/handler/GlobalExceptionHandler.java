package com.xwbing.web.handler;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.exception.BusinessException;
import com.xwbing.service.exception.UtilException;
import com.xwbing.service.util.JsonResult;
import com.xwbing.service.util.dingtalk.DingTalkUtil;
import com.xwbing.starter.exception.ConfigException;
import com.xwbing.starter.exception.PayException;

import lombok.extern.slf4j.Slf4j;

/**
 * 说明:  全局异常处理
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 16:36
 *
 * @author xwbing
 */
// 作用在所有注解了@RequestMapping的控制器的方法上
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 自定义业务异常
     *
     * @param e
     *
     * @return
     */
    // 拦截处理控制器里对应的异常。
    @ExceptionHandler(BusinessException.class)
    // 返回给页面200状态码
    @ResponseStatus(HttpStatus.OK)
    public JSONObject businessException(BusinessException e) {
        log.error(ExceptionUtils.getStackTrace(e));
        return JsonResult.toJSONObj(e.getMessage());
    }

    /**
     * 自定义工具类异常
     *
     * @param e
     *
     * @return
     */
    @ExceptionHandler(UtilException.class)
    public JSONObject utilException(HttpServletResponse response, UtilException e) {
        log.error(ExceptionUtils.getStackTrace(e));
        response.setStatus(HttpStatus.OK.value());
        return JsonResult.toJSONObj(e.getMessage());
    }

    /**
     * 自定义配置异常
     *
     * @param e
     *
     * @return
     */
    @ExceptionHandler(ConfigException.class)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject configException(ConfigException e) {
        log.error(ExceptionUtils.getStackTrace(e));
        return JsonResult.toJSONObj(e.getMessage());
    }

    /**
     * 线上支付异常
     *
     * @param e
     *
     * @return
     */
    @ExceptionHandler(PayException.class)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject payException(PayException e) {
        log.error(ExceptionUtils.getStackTrace(e));
        return JsonResult.toJSONObj(e.getMessage());
    }

    /**
     * CompletableFuture完成结果或任务过程中出现的异常
     *
     * @param e
     *
     * @return
     */
    @ExceptionHandler(CompletionException.class)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject completionException(CompletionException e) {
        log.error(ExceptionUtils.getStackTrace(e));
        return JsonResult.toJSONObj(e.getCause().getMessage());
    }

    /**
     * 请求方式校验
     *
     * @param e
     *
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(ExceptionUtils.getStackTrace(e));
        return JsonResult.toJSONObj("请求方式" + e.getMethod() + "错误,需用" + Arrays.toString(e.getSupportedMethods()) + "请求");
    }

    /**
     * 参数类型不匹配 param=string @RequestParam(required = true) Long param
     *
     * @param request
     * @param e
     *
     * @return
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject requestTypeMismatch(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        log.error("requestTypeMismatch url:{} param:{} type:{}", request.getRequestURI(), e.getName(),
                e.getRequiredType(), e);
        return JsonResult.toJSONObj("参数" + e.getName() + "类型应为" + e.getRequiredType().getSimpleName());
    }

    /**
     * 缺少必传参数 @RequestParam(required = true)
     *
     * @param e
     *
     * @return
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject missingServletRequestParameterException(HttpServletRequest request,
            MissingServletRequestParameterException e) {
        log.error("missingServletRequestParameter url:{} param:{}", request.getRequestURI(), e.getParameterName(), e);
        return JsonResult.toJSONObj("缺少参数:" + e.getParameterName());
    }

    /**
     * 表单检验(validator) 异常 非@RequestBody
     *
     * @param e
     *
     * @return
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject bindException(BindException e) {
        log.error(ExceptionUtils.getStackTrace(e));
        List<ObjectError> list = e.getAllErrors();
        String errorMessages = list.stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("&&"));
        return JsonResult.toJSONObj(errorMessages);
    }

    /**
     * 表单检验(validator) 异常 @RequestBody
     *
     * @param e
     *
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public JSONObject methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(ExceptionUtils.getStackTrace(e));
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        String errorMessages = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("&&"));
        return JsonResult.toJSONObj(errorMessages);
    }

    /**
     * 全局捕获
     *
     * @param e
     *
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JSONObject exception(Exception e) {
        String stackTrace = ExceptionUtils.getStackTrace(e);
        log.error(stackTrace);
        DingTalkUtil.sendRobotMessage("业务流程异常", true, null, stackTrace);
        return JsonResult.toJSONObj("服务器繁忙，请稍后重试！");
    }
}
