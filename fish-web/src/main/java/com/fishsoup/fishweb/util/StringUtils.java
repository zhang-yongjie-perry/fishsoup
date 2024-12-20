package com.fishsoup.fishweb.util;

public class StringUtils {

    public static final String ELLIPSIS = "...";

    public static final String EMPTY = "";

    public static final char PACKAGE_SEPARATOR_CHAR = '.';

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
}
