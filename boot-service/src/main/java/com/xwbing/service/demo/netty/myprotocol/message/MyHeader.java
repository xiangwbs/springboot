package com.xwbing.service.demo.netty.myprotocol.message;

import java.io.Serializable;

import lombok.Data;

@Data
public class MyHeader implements Serializable {
    // 4个字节 头长度
    private int headerLength;
    // 头内容
    private MyHeaderData headerData;
}
