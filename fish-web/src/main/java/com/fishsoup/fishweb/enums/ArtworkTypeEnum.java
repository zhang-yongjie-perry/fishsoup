package com.fishsoup.fishweb.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ArtworkTypeEnum {

    CREATION("0", "文章"),
    MOVIE("1", "影视"),
    IMAGE("2", "图片")
    ;

    /**
     * 反序列化默认情况下会与 Enum#ordinal() 的进行匹配
     * 如果想对枚举类的值进行自定义, 则可以使用 @JsonValue 注解
     */
    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;
    ArtworkTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
