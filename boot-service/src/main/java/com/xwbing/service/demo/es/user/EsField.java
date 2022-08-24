package com.xwbing.service.demo.es.user;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * es字段
 *
 * @author daofeng
 * @version $Id$
 * @since 2022年07月08日 9:28 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EsField {
    /**
     * 字段名称
     *
     * @return
     */
    String value();
}