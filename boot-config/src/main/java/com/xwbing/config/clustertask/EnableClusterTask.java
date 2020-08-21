package com.xwbing.config.clustertask;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

/**
 * @author daofeng
 * @version $
 * @since 2020年08月16日 15:51
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LockProviderConfiguration.class)
@EnableScheduling
@EnableSchedulerLock(defaultLockAtLeastFor = "PT10s", defaultLockAtMostFor = "PT30s")
public @interface EnableClusterTask {
}