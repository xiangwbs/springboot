package com.xwbing.service.demo.netty.myprotocol.message.serializer;

import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson.JSON;

public class JsonSerializable implements ISerializable {
    @Override
    public <T> T deserializable(Class<T> clazz, byte[] bytes) {
        String json = new String(bytes, StandardCharsets.UTF_8);
        return JSON.parseObject(json, clazz);
    }

    @Override
    public <T> byte[] serializable(T object) {
        return JSON.toJSONString(object).getBytes(StandardCharsets.UTF_8);
    }
}