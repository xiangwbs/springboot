package com.xwbing.starter.wx.mp.builder;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * @author Binary Wang(https://github.com/binarywang)
 */
public class TextBuilder extends AbstractBuilder {
    @Override
    public WxMpXmlOutMessage build(String content, WxMpXmlMessage wxMessage, WxMpService service) {
        return WxMpXmlOutMessage.TEXT().content(content).fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
                .build();
    }
}