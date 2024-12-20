package com.fishsoup.fishweb.converter;

import com.fishsoup.fishweb.enums.CreationClassifyEnum;
import org.springframework.core.convert.converter.Converter;

public class CreationClassifyEnumToStringConverter implements Converter<CreationClassifyEnum, String> {
    @Override
    public String convert(CreationClassifyEnum source) {
        return source.getCode();
    }
}
