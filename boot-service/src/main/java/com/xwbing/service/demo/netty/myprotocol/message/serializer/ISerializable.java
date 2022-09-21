package com.xwbing.service.demo.netty.myprotocol.message.serializer;

public interface ISerializable {
    /**
     * 反序列化
     *
     * @param clazz
     * @param bytes
     * @param <T>
     *
     * @return
     */
    <T> T deserializable(Class<T> clazz, byte[] bytes);

    /**
     * 序列化
     *
     * @param object
     * @param <T>
     *
     * @return
     */
    <T> byte[] serializable(T object);
}