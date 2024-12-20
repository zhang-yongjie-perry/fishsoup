package com.fishsoup.fishums.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserTypeEnum {
    COMMON("0", "普通用户"),
    ADMIN("1", "管理员")
    ;

    @EnumValue
    private final String code;

    @JsonValue
    private final String desc;

    UserTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
