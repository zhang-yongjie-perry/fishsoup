package com.fishsoup.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    String name() default "";

    /**
     * 默认 -1 开启看门狗, 否则超时将释放锁
     */
    int leaseTime() default -1;

    int waitTime() default 0;
}
