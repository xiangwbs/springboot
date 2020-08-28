package com.xwbing.web.handler;

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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.starter.util.dingtalk.DingTalkClient;
import com.xwbing.service.exception.BusinessException;
import com.xwbing.service.exception.PayException;
import com.xwbing.service.exception.UtilException;
import com.xwbing.service.util.JsonResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 说明:  全局异常处理
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 16:36
 * @author xwbing
 */
// 作用在所有注解了@RequestMapping的控制器的方法上
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 自定义业务异常
     *
     * @param ex BusinessException
     * @return
     */
    // 拦截处理控制器里对应的异常。
    @ExceptionHandler(value = BusinessException.class)
    // 返回给页面200状态码
    @ResponseStatus(value = HttpStatus.OK)
    public JSONObject handlerBusinessException(BusinessException ex) {
        log.error(ExceptionUtils.getStackTrace(ex));
        return JsonResult.toJSONObj(ex.getMessage());
    }

    /**
     * 自定义工具类异常
     *
     * @param ex UtilException
     * @return
     */
    @ExceptionHandler(value = UtilException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public JSONObject handlerUtilException(UtilException ex) {
        log.error(ExceptionUtils.getStackTrace(ex));
        return JsonResult.toJSONObj(ex.getMessage());
    }

    /**
     * 线上支付异常
     *
     * @param ex PayException
     * @return
     */
    @ExceptionHandler(value = PayException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public JSONObject handlerPayException(PayException ex) {
        log.error(ExceptionUtils.getStackTrace(ex));
        return JsonResult.toJSONObj(ex.getMessage());
    }

    /**
     * CompletableFuture完成结果或任务过程中出现的异常
     *
     * @param ex CompletionException
     * @return
     */
    @ExceptionHandler(value = CompletionException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public JSONObject handlerCompletionException(CompletionException ex) {
        log.error(ExceptionUtils.getStackTrace(ex));
        Throwable cause = ex.getCause();
        String errorMessages = cause.getMessage();
        String detail = cause.toString();
        if (!detail.contains("BusinessException")) {
            errorMessages = "异步获取数据出错";
        }
        return JsonResult.toJSONObj(errorMessages);
    }

    /**
     * 表单检验(validator) 异常  非@RequestBody
     *
     * @param request
     * @param response
     * @param ex       BindException
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    public JSONObject handlerBindException(HttpServletRequest request, HttpServletResponse response, BindException ex) {
        log.error(ExceptionUtils.getStackTrace(ex));
        List<ObjectError> list = ex.getAllErrors();
        String errorMessages = list.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("&&"));
        response.setStatus(HttpStatus.OK.value());
        return JsonResult.toJSONObj(errorMessages);
    }

    /**
     * 表单检验(validator) 异常 @RequestBody
     *
     * @param ex MethodArgumentNotValidException
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.OK)
    public JSONObject handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ExceptionUtils.getStackTrace(ex));
        BindingResult bindingResult = ex.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        String errorMessages = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("&&"));
        return JsonResult.toJSONObj(errorMessages);
    }

    /**
     * 全部捕获
     *
     * @param ex Exception
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public JSONObject handlerException(Exception ex) {
        String stackTrace = ExceptionUtils.getStackTrace(ex);
        log.error(stackTrace);
        DingTalkClient.sendRobotMessage("业务流程异常", true, null, stackTrace);
        return JsonResult.toJSONObj("系统异常,请联系管理员");
    }
}
