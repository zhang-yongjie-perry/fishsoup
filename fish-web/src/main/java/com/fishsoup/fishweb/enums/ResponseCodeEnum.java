package com.fishsoup.fishweb.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * http请求返回状态码枚举类
 * 0: 成功, 1: 失败
 *
 * @author zhang_yjie
 * @date 2024-10-18
 */
@Getter
public enum ResponseCodeEnum {
    SUCCESS("0", "成功"),
    FAILURE("1", "失败"),
    ;

    @JsonValue
    private final String code;
    private final String desc;

    ResponseCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
