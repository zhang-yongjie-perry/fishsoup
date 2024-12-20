package com.fishsoup.fishweb.annotation;

import com.fishsoup.fishweb.enums.ArtworkTypeEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FootstepLog {
    ArtworkTypeEnum value() default ArtworkTypeEnum.CREATION;
}
