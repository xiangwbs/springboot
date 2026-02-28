/**
 * Copyright(C) 2018 Hangzhou Fugle Technology Co., Ltd. All rights reserved.
 */
package com.xwbing.service.demo.ws;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author MaoJizhang
 * @version $Id$
 * @since 2018年12月26日 上午11:34:03
 */
@Data
@Builder
public class WsClientMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private String message;
    private String userId;
    private String destination;
    private MsgType msgType;

    public enum MsgType {
        USER_MSG,
        TOPIC_MSG
    }

    public static WsClientMessage buildTopicMsg(String destination, String msg) {
        return WsClientMessage.builder().msgType(MsgType.TOPIC_MSG).destination(destination).message(msg).build();
    }

    public static WsClientMessage buildUserMsg(String userId, String destination, String msg) {
        return WsClientMessage.builder().msgType(MsgType.USER_MSG).userId(userId).destination(destination).message(msg).build();
    }
}