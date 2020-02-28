package com.xwbing.handler;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.exception.BusinessException;
import com.xwbing.util.JsonResult;
import org.apache.shiro.authc.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

/**
 * 说明:  全局异常处理
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
// 作用在所有注解了@RequestMapping的控制器的方法上
@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 自定义业务异常
     *
     * @param ex
     * @return
     */
    // 拦截处理控制器里对应的异常。
    @ExceptionHandler(value = BusinessException.class)
    // 返回给页面200状态码
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public JSONObject handlerBusinessException(BusinessException ex) {
        logger.error(ex.getMessage());
        return JsonResult.toJSONObj(ex.getMessage());
    }

    /**
     * shiro登录认证异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public JSONObject handlerAuthenticationException(AuthenticationException ex) {
        logger.error(ex.getMessage());
        return JsonResult.toJSONObj(ex.getMessage());
    }

    /**
     * completableFuture完成结果或任务过程中出现的异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = CompletionException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public JSONObject handlerCompletionException(CompletionException ex) {
        Throwable cause = ex.getCause();
        String errorMessages = cause.getMessage();
        logger.error(errorMessages);
        String detail = cause.toString();
        if (!detail.contains("BusinessException")) {
            errorMessages = "异步获取数据出错";
        }
        return JsonResult.toJSONObj(errorMessages);
    }

    /**
     * 表单检验(validator) 异常
     *
     * @param request
     * @param response
     * @param ex
     * @return
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public JSONObject handlerBindException(HttpServletRequest request, HttpServletResponse response, BindException ex) {
        List<ObjectError> list = ex.getAllErrors();
        String errorMessages = list.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("&&"));
        logger.error(errorMessages);
        response.setStatus(HttpStatus.OK.value());
        return JsonResult.toJSONObj(errorMessages);
    }

    /**
     * 表单检验(validator) 异常 (@RequestBody)
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public JSONObject handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        String errorMessages = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining("&&"));
        logger.error(errorMessages);
        return JsonResult.toJSONObj(errorMessages);
    }

    /**
     * 全部捕获
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public JSONObject handlerException(Exception ex) {
        logger.error(ex.getMessage());
        return JsonResult.toJSONObj("系统异常，请联系管理员");
    }
}
