package com.fishsoup.fishweb.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 状态枚举类
 * 1: 正常, 0: 停用
 *
 * @author zhang_yjie
 * @date 2024-10-22
 */
@Getter
public enum UserStatusEnum {
    DISABLED("0", "停用"),
    NORMAL("1", "正常"),
    ;

    @JsonValue
    private final String code;
    private final String desc;

    UserStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
