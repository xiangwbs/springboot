package com.xwbing.service.demo.netty.myprotocol.message;

import java.io.Serializable;

import lombok.Data;

@Data
public class MyHeaderData implements Serializable {
    // 版本 4个字节
    private int version;
    // 语言 1个字节
    private byte languageCode;
    // 序列化方式 1个字节
    private byte serializableType;
    // 请求类型 1个字节
    private byte reqType;
}
