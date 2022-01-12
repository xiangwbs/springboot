package com.xwbing.web.controller.wx;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.web.response.ApiResponse;
import com.xwbing.web.response.ApiResponseUtil;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaMessage;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.constant.WxMaConstants;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/wx/ma/portal/{appId}")
public class WxMaPortalController {
    private final WxMaService wxMaService;
    private final WxMaMessageRouter wxMaMessageRouter;

    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(@PathVariable String appId,
            @RequestParam(name = "signature", required = false) String signature,
            @RequestParam(name = "timestamp", required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {
        log.info("接收到来自微信服务器的认证消息：signature = [{}], timestamp = [{}], nonce = [{}], echostr = [{}]", signature,
                timestamp, nonce, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }
        if (wxMaService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }
        return "非法请求";
    }

    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@PathVariable String appId, @RequestBody String requestBody,
            @RequestParam(name = "msg_signature", required = false) String msgSignature,
            @RequestParam(name = "encrypt_type", required = false) String encryptType,
            @RequestParam(name = "signature", required = false) String signature,
            @RequestParam("timestamp") String timestamp, @RequestParam("nonce") String nonce) {
        log.info("接收微信请求：[msg_signature=[{}], encrypt_type=[{}], signature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ", msgSignature, encryptType, signature, timestamp,
                nonce, requestBody);
        final boolean isJson = Objects
                .equals(wxMaService.getWxMaConfig().getMsgDataFormat(), WxMaConstants.MsgDataFormat.JSON);
        WxMaMessage inMessage;
        if (StringUtils.isBlank(encryptType)) {
            // 明文传输的消息
            if (isJson) {
                inMessage = WxMaMessage.fromJson(requestBody);
            } else {
                inMessage = WxMaMessage.fromXml(requestBody);
            }
            this.route(inMessage, appId);
            return "success";
        } else if ("aes".equals(encryptType)) {
            // 是aes加密的消息
            if (isJson) {
                inMessage = WxMaMessage.fromEncryptedJson(requestBody, wxMaService.getWxMaConfig());
            } else {
                inMessage = WxMaMessage
                        .fromEncryptedXml(requestBody, wxMaService.getWxMaConfig(), timestamp, nonce, msgSignature);
            }
            this.route(inMessage, appId);
            return "success";
        }
        throw new RuntimeException("不可识别的加密类型：" + encryptType);
    }

    private void route(WxMaMessage message, String appId) {
        try {
            wxMaMessageRouter.route(message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/getUserInfo")
    public ApiResponse<WxMaUserInfo> getUserInfo(@RequestParam String code, @RequestParam String signature,
            @RequestParam String rawData, @RequestParam String encryptedData, @RequestParam String iv)
            throws WxErrorException {
        WxMaUserService userService = wxMaService.getUserService();
        WxMaJscode2SessionResult session = userService.getSessionInfo(code);
        // 用户信息校验
        if (!userService.checkUserInfo(session.getSessionKey(), rawData, signature)) {
            return null;
        }
        // 解密用户信息
        WxMaUserInfo userInfo = userService.getUserInfo(session.getSessionKey(), encryptedData, iv);
        return ApiResponseUtil.success(userInfo);

    }

    @ApiOperation("获取用户绑定手机号信息")
    @GetMapping("/getPhoneNoInfo")
    public ApiResponse<String> getPhoneNoInfo(@RequestParam String code, @RequestParam String signature,
            @RequestParam String rawData, @RequestParam String encryptedData, @RequestParam String iv)
            throws WxErrorException {
        WxMaUserService userService = wxMaService.getUserService();
        WxMaJscode2SessionResult session = userService.getSessionInfo(code);
        // 用户信息校验
        if (!userService.checkUserInfo(session.getSessionKey(), rawData, signature)) {
            return null;
        }
        // 解密用户手机号
        String purePhoneNumber = userService.getPhoneNoInfo(session.getSessionKey(), encryptedData, iv)
                .getPurePhoneNumber();
        return ApiResponseUtil.success(purePhoneNumber);
    }
}