package com.xwbing.service.demo.netty.customprotocol.message.enums;

public enum LanguageEnum {
    JAVA((byte)0),
    PYTHON((byte)1),
    GO((byte)2);

    private byte code;

    LanguageEnum(byte code) {
        this.code = code;
    }

    public byte code(){
        return this.code;
    }
}
