package com.dixon.allbase.fun;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Create by: dixon.xu
 * Create on: 2020.07.13
 * Functional desc: 时间工具类
 */
public class TimeUtil {

    /**
     * 是否是相同天
     */
    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 是否是今天
     */
    public static boolean isToday(long time) {
        return isThisTime(time, "yyyy-MM-dd");
    }

    /**
     * 是否是昨天
     */
    public static boolean isYesterday(long time) {
        boolean isYesterday = false;
        Date date = new Date();
        if (time < date.getTime() && time > (date.getTime() - 24 * 60 * 60 * 1000)) {
            isYesterday = true;
        }
        return isYesterday;
    }

    /**
     * 是否是当月
     */
    public static boolean isCurrentMonth(long time) {
        return isThisTime(time, "yyyy-MM");
    }

    public static boolean isThisTime(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        if (param.equals(now)) {
            return true;
        }
        return false;
    }
}
