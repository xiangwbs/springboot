package com.xwbing.service.demo.netty.codec.protocol.message;

import java.io.Serializable;

import lombok.Data;

@Data
public class MyHeaderData implements Serializable {
    private int version;//4个字节
    private byte languageCode;// 1个字节
    private byte serializableType;// 序列化方式 1个字节
    private byte reqType;//请求类型 1个字节
}
