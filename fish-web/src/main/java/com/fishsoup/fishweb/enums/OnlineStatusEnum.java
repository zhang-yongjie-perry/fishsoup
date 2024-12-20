package com.fishsoup.fishweb.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 账号在线状态枚举类
 * 0: 离线, 1: 在线
 *
 * @author zhang_yjie
 * @date 2024-10-18
 */
@Getter
public enum OnlineStatusEnum {

    OFFLINE("0", "离线"),
    ONLINE("1", "在线"),
    ;

    @EnumValue
    private final String code;

    @JsonValue
    private final String desc;

    OnlineStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
