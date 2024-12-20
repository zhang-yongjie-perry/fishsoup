package com.fishsoup.fishums.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 性别枚举类
 * 0: 男, 1: 女
 *
 * @author zhang_yjie
 * @date 2024-10-18
 */
@Getter
public enum SexEnum {

    MALE("0", "男"),
    FEMALE("1", "女"),
    SECRECY("2", "保密"),
    UNKNOWN("3", "未知"),
    ;

    @EnumValue
    private final String code;

    @JsonValue
    private final String desc;

    SexEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
