package com.fishsoup.util;

import com.fishsoup.entity.exception.BusinessException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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

    public static Date parse(String dateStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new BusinessException("日期解析异常:" + e.getMessage());
        }
    }

    public static long getSameDayExpirationTime() {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 获取今天的午夜时间
        LocalDateTime midnight = LocalDateTime.of(now.toLocalDate(), LocalTime.MIDNIGHT)
            .plusDays(1); // 明天的午夜即为今晚的午夜之后的时间

        // 计算时间差并转换为秒
        return ChronoUnit.SECONDS.between(now, midnight);
    }
}
