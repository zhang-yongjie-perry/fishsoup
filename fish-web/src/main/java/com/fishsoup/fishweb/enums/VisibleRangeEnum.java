package com.fishsoup.fishweb.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum VisibleRangeEnum {

    PRIVATE("1", "私密"),
    PUBLIC("2", "公开"),
    ;

    @JsonValue
    private final String code;
    private final String desc;

    VisibleRangeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static VisibleRangeEnum fromCode(String code) {
        for (VisibleRangeEnum visibleRangeEnum : VisibleRangeEnum.values()) {
            if (Objects.equals(visibleRangeEnum.getCode(), code)) {
                return visibleRangeEnum;
            }
        }
        throw new IllegalArgumentException(code);
    }

    public static boolean isVisibleRangeEnum(String code) {
        for (VisibleRangeEnum visibleRangeEnum : VisibleRangeEnum.values()) {
            if (Objects.equals(visibleRangeEnum.getCode(), code)) {
                return true;
            }
        }
        return false;
    }
}
