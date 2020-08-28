package com.xwbing.starter.clusterseq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * @author daofeng
 * @version $
 * @since 2020年01月03日 11:53
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ClusterSeqAutoConfiguration.class)
public @interface EnableClusterSeq {
}
