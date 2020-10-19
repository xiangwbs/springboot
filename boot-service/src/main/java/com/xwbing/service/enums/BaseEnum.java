package com.xwbing.service.enums;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年09月11日 5:47 PM
 */
// @JsonFormat(shape = Shape.OBJECT)
// @JsonDeserialize(using = CodeEnumDeserializer.class)
public interface BaseEnum {
    int getCode();
}
