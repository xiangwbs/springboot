package com.xwbing.service.demo.netty.codec.protocol.message.enums;

public enum SerializerEnum {
    JAVA((byte) 0),
    JSON((byte) 1);

    private byte code;

    SerializerEnum(byte code) {
        this.code = code;
    }

    public byte code() {
        return this.code;
    }

    public static SerializerEnum valueOf(byte code) {
        for (SerializerEnum enumSerializer : SerializerEnum.values()) {
            if(enumSerializer.code == code){
                return enumSerializer;
            }
        }
        return null;
    }
}
