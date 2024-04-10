package com.woowacamp.soolsool.global.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;
import org.springframework.core.annotation.Order;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Order(value = 1)
public @interface DistributedLock {
    String entityId() default "";
    String lockName() default "";
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    long waitTime() default 1000L;

    long leaseTime() default 10000L;
}
