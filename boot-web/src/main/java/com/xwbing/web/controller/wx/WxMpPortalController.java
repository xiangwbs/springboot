package com.xwbing.web.controller.wx;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.service.exception.BusinessException;
import com.xwbing.web.response.ApiResponse;
import com.xwbing.web.response.ApiResponseUtil;

import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.api.WxConsts.EventType;
import me.chanjar.weixin.common.api.WxConsts.XmlMsgType;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.service.WxOAuth2Service;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpQrcodeService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/wx/mp/portal/{appId}")
public class WxMpPortalController {
    private final WxMpService wxMpService;
    private final WxMpMessageRouter wxMpMessageRouter;

    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(@PathVariable String appId, @RequestParam(required = false) String timestamp,
            @RequestParam(required = false) String nonce, @RequestParam(required = false) String signature,
            @RequestParam(required = false) String echostr) {
        log.info("WxMpPortalController authGet timestamp:{} nonce:{} signature:{} echostr:{}", timestamp, nonce,
                signature, echostr);
        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            log.error("WxMpPortalController authGet error param");
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }
        if (wxMpService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        } else {
            log.error("WxMpPortalController authGet checkSignature fail");
        }
        return "非法请求";
    }

    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@PathVariable String appId, @RequestParam String timestamp, @RequestParam String nonce,
            @RequestParam String signature, @RequestParam String openid, @RequestBody String requestBody,
            @RequestParam(name = "encrypt_type", required = false) String encType,
            @RequestParam(name = "msg_signature", required = false) String msgSignature) {
        log.info(
                "WxMpPortalController post timestamp:{} nonce:{} signature:{} encType:{} msgSignature:{} openid:{} requestBody:{}",
                timestamp, nonce, signature, encType, msgSignature, openid, requestBody);
        if (!wxMpService.checkSignature(timestamp, nonce, signature)) {
            log.error("WxMpPortalController post checkSignature fail");
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }
        // String out = null;
        if (encType == null) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            this.handle(inMessage);
            // WxMpXmlOutMessage outMessage = this.route(inMessage);
            // if (outMessage == null) {
            //     return "";
            // }
            // out = outMessage.toXml();
        } else if ("aes".equalsIgnoreCase(encType)) {
            // aes加密的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage
                    .fromEncryptedXml(requestBody, wxMpService.getWxMpConfigStorage(), timestamp, nonce, msgSignature);
            log.info("WxMpPortalController post decryptedXml:{}", inMessage.toString());
            this.handle(inMessage);
            // log.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
            // WxMpXmlOutMessage outMessage = this.route(inMessage);
            // if (outMessage == null) {
            //     return "";
            // }
            // out = outMessage.toEncryptedXml(wxMpService.getWxMpConfigStorage());
        }
        // log.debug("\n组装回复信息：{}", out);
        // return out;
        return "success";
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return this.wxMpMessageRouter.route(message);
        } catch (Exception e) {
            log.error("路由消息时出现异常！", e);
        }
        return null;
    }

    public void handle(WxMpXmlMessage message) {
        String msgType = message.getMsgType();
        if (XmlMsgType.EVENT.equals(msgType)) {
            this.handleEvent(message);
        }
    }

    private void handleEvent(WxMpXmlMessage message) {
        WxMpUser wxMpUser;
        String openId = message.getFromUser();
        try {
            wxMpUser = wxMpService.getUserService().userInfo(openId);
        } catch (WxErrorException e) {
            log.error("WxMpPortalController handleEvent errorCode:{} errorMsg:{}", e.getError().getErrorCode(),
                    e.getError().getErrorMsg());
            return;
        }
        String event = message.getEvent();
        String eventKey = message.getEventKey();
        switch (event) {
            case WxConsts.EventType.SUBSCRIBE:
                if (StringUtils.isNotEmpty(eventKey) && eventKey.startsWith("qrscene_")) {
                }
                break;
            case EventType.UNSUBSCRIBE:
                break;
            case EventType.SCAN:
                if (Boolean.TRUE.equals(wxMpUser.getSubscribe())) {

                }
                break;
            default:
                break;

        }
    }

    @ApiOperation("获取Jsapi签名")
    @GetMapping("/getJsApiSignature")
    public ApiResponse<WxJsapiSignature> getJsApiSignature(@RequestParam String url) throws WxErrorException {
        return ApiResponseUtil.success(wxMpService.createJsapiSignature(url));
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/getUserInfo")
    public ApiResponse<WxOAuth2UserInfo> getUserInfo(@RequestParam String code) throws WxErrorException {
        WxOAuth2Service oAuth2Service = wxMpService.getOAuth2Service();
        WxOAuth2AccessToken accessToken = oAuth2Service.getAccessToken(code);
        return ApiResponseUtil.success(oAuth2Service.getUserInfo(accessToken, null));
    }

    @ApiOperation("获取二维码")
    @GetMapping("/getQrcode")
    public String getMpQrcode() {
        String sceneStr = IdUtil.fastSimpleUUID();
        try {
            WxMpQrcodeService qrcodeService = wxMpService.getQrcodeService();
            String ticket = qrcodeService.qrCodeCreateTmpTicket(sceneStr, 60 * 10).getTicket();
            return qrcodeService.qrCodePictureUrl(ticket);
        } catch (WxErrorException e) {
            log.error("getQrcode errorCode:{} errorMsg:{}", e.getError().getErrorCode(), e.getError().getErrorMsg());
            throw new BusinessException("获取微信二维码失败");
        }
    }
}