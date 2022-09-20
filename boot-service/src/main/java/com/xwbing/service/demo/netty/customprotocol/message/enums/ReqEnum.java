package com.xwbing.service.demo.netty.customprotocol.message.enums;

public enum ReqEnum {
    REQ((byte)0),
    RES((byte)1),
    PING((byte)2),
    PONG((byte)3);

    private byte code;

    ReqEnum(byte code) {
        this.code = code;
    }

    public byte code(){
        return this.code;
    }
}
