package com.xwbing.service.enums;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年09月11日 5:47 PM
 */
@JsonFormat(shape = Shape.OBJECT)
@JsonDeserialize(using = CodeEnumDeserializer.class)
public interface BaseEnum extends Serializable {
    int getCode();
}
