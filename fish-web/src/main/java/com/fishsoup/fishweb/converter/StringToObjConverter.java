package com.fishsoup.fishweb.converter;

import com.fishsoup.fishweb.enums.CreationClassifyEnum;
import com.fishsoup.fishweb.enums.VisibleRangeEnum;
import org.springframework.core.convert.converter.Converter;

public class StringToObjConverter implements Converter<String, Object> {
    @Override
    public Object convert(String source) {
        if (CreationClassifyEnum.isCreationClassifyEnum(source)) {
            return CreationClassifyEnum.fromCode(source);
        }
        if (VisibleRangeEnum.isVisibleRangeEnum(source)) {
            return VisibleRangeEnum.fromCode(source);
        }
        return source;
    }
}
