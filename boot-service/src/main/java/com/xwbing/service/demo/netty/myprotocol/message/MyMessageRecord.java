package com.xwbing.service.demo.netty.myprotocol.message;

import java.io.Serializable;

import lombok.Data;

@Data
public class MyMessageRecord implements Serializable {
    // 4个字节 消息长度
    private int length;
    // 消息头
    private MyHeader header;
    // 消息体
    private Object body;
}