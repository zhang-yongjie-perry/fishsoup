package com.fishsoup.converter;

import com.fishsoup.enums.VisibleRangeEnum;
import org.springframework.core.convert.converter.Converter;

public class StringToVisibleRangeEnumConverters implements Converter<String, VisibleRangeEnum> {
    @Override
    public VisibleRangeEnum convert(String source) {
        return VisibleRangeEnum.fromCode(source);
    }
}
