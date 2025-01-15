package com.fishsoup.converter;

import com.fishsoup.enums.VisibleRangeEnum;
import org.springframework.core.convert.converter.Converter;

public class VisibleRangeEnumToStringConverters implements Converter<VisibleRangeEnum, String> {
    @Override
    public String convert(VisibleRangeEnum source) {
        return source.getCode();
    }
}
