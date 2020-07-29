package com.dixon.allbase.fun;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Create by: dixon.xu
 * Create on: 2020.06.16
 * Functional desc: 将时间转换为NoteApp需要的格式
 */
public class TimeFormat {

    /**
     * 将[long]转为[yyyy.MM.dd HH:mm]格式字符串
     */
    public static String format(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.CHINA);
        Date date = new Date(time);
        return format.format(date);
    }

    /**
     * 将[long]转为[yyyy年MM月dd日]格式字符串
     */
    public static String formatChina(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
        Date date = new Date(time);
        return format.format(date);
    }

    public static int hour(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public static int minute(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        int minute = c.get(Calendar.MINUTE);
        return minute;
    }

    public static String dayDesc() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH", Locale.CHINA);
        String str = df.format(date);
        int a = Integer.parseInt(str);
        if (a >= 0 && a <= 12) {
            return "早上好";
        }
        if (a > 12 && a <= 18) {
            return "下午好";
        }
        if (a > 18 && a <= 24) {
            return "晚上好";
        }
        return "";
    }
}
