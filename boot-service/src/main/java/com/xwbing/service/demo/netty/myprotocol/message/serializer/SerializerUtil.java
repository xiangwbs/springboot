package com.xwbing.service.demo.netty.myprotocol.message.serializer;

import com.xwbing.service.demo.netty.myprotocol.message.enums.SerializerEnum;

public class SerializerUtil {
    public static <T> T deserializable(Class<T> clazz, byte[] bytes, byte serializerType) {
        SerializerEnum enumSerializer = SerializerEnum.valueOf(serializerType);
        switch (enumSerializer) {
            case JAVA:
                return new JavaSerializable().deserializable(clazz, bytes);
            case JSON:
                return new JsonSerializable().deserializable(clazz, bytes);
            default:
                break;
        }
        return null;
    }

    public static <T> byte[] serializable(T object, byte serializerType) {
        SerializerEnum enumSerializer = SerializerEnum.valueOf(serializerType);
        switch (enumSerializer) {
            case JAVA:
                return new JavaSerializable().serializable(object);
            case JSON:
                return new JsonSerializable().serializable(object);
            default:
                break;
        }
        return null;
    }
}