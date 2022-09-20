package com.xwbing.service.demo.netty.codec.protocol.message;

import java.io.Serializable;

import lombok.Data;

@Data
public class MyHeader implements Serializable {
    private int headerLength;// 4个字节
    private MyHeaderData headerData;
}
