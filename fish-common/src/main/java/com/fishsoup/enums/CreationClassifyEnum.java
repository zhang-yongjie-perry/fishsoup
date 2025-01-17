package com.fishsoup.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Objects;

@Getter
public enum CreationClassifyEnum {

    MAJOR("1", "专业"),
    LITERATURE("2", "文学"),
    ESSAY("3", "随笔"),
    ;

    @JsonValue
    private final String code;
    private final String desc;

    CreationClassifyEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonCreator
    public static CreationClassifyEnum fromCode(String code) {
        for (CreationClassifyEnum creationClassifyEnum : CreationClassifyEnum.values()) {
            if (Objects.equals(creationClassifyEnum.getCode(), code)) {
                return creationClassifyEnum;
            }
        }
        throw new IllegalArgumentException(code);
    }

    public static boolean isCreationClassifyEnum(String name) {
        for (CreationClassifyEnum creationClassifyEnum : CreationClassifyEnum.values()) {
            if (Objects.equals(creationClassifyEnum.name(), name)) {
                return true;
            }
        }
        return false;
    }
}
