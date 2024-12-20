package com.fishsoup.enums;

import lombok.Getter;

/**
 * 影视状态枚举类
 */
@Getter
public enum MovieStatusEnum {
    INIT(0, "初始化"),
    COMPLETED(1, "已构建"),
    ;
    private final int code;
    private final String value;
    MovieStatusEnum(int code, String value) {
        this.code = code;
        this.value = value;
    }

}
