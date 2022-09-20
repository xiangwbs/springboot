package com.xwbing.service.demo.netty.codec.protocol.message;

import java.io.Serializable;

import lombok.Data;

@Data
public class MyMessageRecord implements Serializable {
    private int length;// 4个字节
    private MyHeader header;
    private Object body;
}
