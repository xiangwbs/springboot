package com.xwbing.service.enums.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * 基础枚举
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年09月11日 5:47 PM
 */
@JsonFormat(shape = Shape.OBJECT)// jackSon序列化返回枚举对象
@JsonDeserialize(using = JacksonDeserializer.class)// jackSon反序列化处理
public interface BaseEnum {
    int getCode();
}