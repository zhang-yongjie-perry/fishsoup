package com.fishsoup.fishweb.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum StatusEnum {
    SUCCESS(0, "成功"),
    FAILURE(1, "失败"),
    ;
    @JsonValue
    private final int code;
    private final String desc;

    StatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
