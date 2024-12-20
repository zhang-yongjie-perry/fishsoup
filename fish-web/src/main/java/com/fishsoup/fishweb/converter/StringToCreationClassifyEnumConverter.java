package com.fishsoup.fishweb.converter;

import com.fishsoup.fishweb.enums.CreationClassifyEnum;
import org.springframework.core.convert.converter.Converter;

public class StringToCreationClassifyEnumConverter implements Converter<String, CreationClassifyEnum> {
    @Override
    public CreationClassifyEnum convert(String source) {
        return CreationClassifyEnum.fromCode(source);
    }
}
