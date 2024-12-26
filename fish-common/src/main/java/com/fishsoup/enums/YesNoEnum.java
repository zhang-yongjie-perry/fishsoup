package com.fishsoup.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum YesNoEnum {
    NO("0", "否"),
    YES("1", "是"),
    ;

    YesNoEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @EnumValue
    private final String code;
    @JsonValue
    private final String desc;
}
