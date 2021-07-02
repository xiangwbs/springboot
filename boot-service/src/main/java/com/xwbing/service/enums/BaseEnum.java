package com.xwbing.service.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年09月11日 5:47 PM
 */
// jackSon序列化返回枚举对象
@JsonFormat(shape = Shape.OBJECT)
// jackSon反序列化处理
@JsonDeserialize(using = JacksonDeserializer.class)
public interface BaseEnum {
    int getCode();
}