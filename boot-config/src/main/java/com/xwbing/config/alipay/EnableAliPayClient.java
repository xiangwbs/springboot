package com.xwbing.config.alipay;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年07月24日 下午10:43
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(AliPayConfiguration.class)
public @interface EnableAliPayClient {
}
