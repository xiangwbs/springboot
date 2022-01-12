package com.xwbing.starter.wx.mp.handler;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
public class LogHandler extends AbstractHandler {
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
            WxSessionManager sessionManager) {
        this.logger.info("接收到请求消息，内容：{}", JSONObject.toJSONString(wxMessage));
        return null;
    }
}