package com.fishsoup.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static final String ELLIPSIS = "...";

    public static final String EMPTY = "";

    public static final char PACKAGE_SEPARATOR_CHAR = '.';

    public static final String PACKAGE_COLON_CHAR = ":";

    public static final String NUM_REGEX = "\\d+";


    public static String cleanPath(String path) {
        return org.springframework.util.StringUtils.cleanPath(path);
    }

    public static String convertCamelToUnderscore(String camelCaseStr) {
        if (!org.springframework.util.StringUtils.hasText(camelCaseStr)) {
            return camelCaseStr;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCaseStr.length(); i++) {
            char currentChar = camelCaseStr.charAt(i);
            if (!Character.isUpperCase(currentChar)) {
                result.append(currentChar);
                continue;
            }
            if (i != 0 && !Character.isUpperCase(camelCaseStr.charAt(i - 1))) {
                result.append('_');
            }
            result.append(Character.toLowerCase(currentChar));
        }
        return result.toString();
    }

    public static boolean hasText(String str) {
        return org.springframework.util.StringUtils.hasText(str);
    }

    public static Integer parseInt(String str) {
        if (Objects.isNull(str)) {
            return null;
        }
        Matcher matcher = Pattern.compile(NUM_REGEX).matcher(str);
        return matcher.find() ? Integer.parseInt(matcher.group(0)) : null;
    }
}
