package com.xwbing.service.enums;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年09月11日 5:47 PM
 */
// @JsonFormat(shape = Shape.OBJECT) //序列化返回枚举对象
// @JsonDeserialize(using = JacksonDeserializer.class)
public interface BaseEnum {
    int getCode();
}