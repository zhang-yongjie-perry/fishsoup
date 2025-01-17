package com.fishsoup.converter;

import com.fishsoup.enums.CreationClassifyEnum;
import com.fishsoup.enums.VisibleRangeEnum;
import org.springframework.core.convert.converter.Converter;

public class StringToObjConverter implements Converter<String, Object> {
    @Override
    public Object convert(String source) {
        if (CreationClassifyEnum.isCreationClassifyEnum(source)) {
            return CreationClassifyEnum.valueOf(source);
        }
        if (VisibleRangeEnum.isVisibleRangeEnum(source)) {
            return VisibleRangeEnum.valueOf(source);
        }
        return source;
    }
}
