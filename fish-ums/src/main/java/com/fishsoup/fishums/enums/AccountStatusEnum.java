package com.fishsoup.fishums.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 账号使用状态枚举类
 * 0: 失效, 1: 正常, 2: 锁定
 *
 * @author zhang_yjie
 * @date 2024-10-18
 */
@Getter
public enum AccountStatusEnum {

    EXPIRED("0", "失效"),
    NORMAL("1", "正常"),
    LOCKED("2", "锁定"),
    ;

    @EnumValue
    private final String code;

    @JsonValue
    private final String desc;

    AccountStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
