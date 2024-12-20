package com.fishsoup.fishums.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
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

    @EnumValue
    private final String code;
    @JsonValue
    private final String desc;

    UserStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
