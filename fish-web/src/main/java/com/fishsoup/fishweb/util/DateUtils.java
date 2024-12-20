package com.fishsoup.fishweb.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static final String YYYY_MM_DD_HH_MI_SS = "yyyy-MM-dd HH:mm:ss";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static Date now() {
        return new Date();
    }

    public static String now(String format) {
        return formatDate(now(), format);
    }

    public static String formatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static Date addTime(Date date, long time) {
        return new Date(date.getTime() + time);
    }

    public static int compareTo(Date date1, Date date2) {
        return date1.compareTo(date2);
    }
}
