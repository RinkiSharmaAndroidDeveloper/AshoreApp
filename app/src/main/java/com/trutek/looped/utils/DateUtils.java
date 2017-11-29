package com.trutek.looped.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
    public static final long YEAR_IN_MILLIS = DAY_IN_MILLIS * 365;

    private static final SimpleDateFormat DAY_AND_MONTH_AND_YEAR_FULL_FORMAT;
    private static final SimpleDateFormat SHORT_DATE_WITHOUT_DIVIDERS_FORMAT;
    private static final SimpleDateFormat DAY_AND_MONTH_AND_YEAR_FORMAT;
    private static final SimpleDateFormat SIMPLE_TIME_FORMAT;

    static {

        SHORT_DATE_WITHOUT_DIVIDERS_FORMAT = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
        DAY_AND_MONTH_AND_YEAR_FULL_FORMAT = new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault());
        DAY_AND_MONTH_AND_YEAR_FORMAT = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        SIMPLE_TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }

    public static long nowSeconds() {
        return nowMillis() / SECOND_IN_MILLIS;
    }

    public static long nowMillis() {
        return getCalendar().getTimeInMillis();
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance(TimeZone.getDefault());
    }

    public static Calendar getCalendar(long seconds) {
        Calendar calendar = getCalendar();
        calendar.setTimeInMillis(seconds * SECOND_IN_MILLIS);
        return calendar;
    }

    public static long roundToDays(long seconds) {
        Calendar c = getCalendar(seconds);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        return c.getTimeInMillis() / SECOND_IN_MILLIS;
    }

    private static String toTodayYesterdayDateByFormat(long seconds, SimpleDateFormat simpleDateFormat) {
        long today = roundToDays(nowSeconds());
        long inputDay = roundToDays(seconds);

        if (inputDay == today) {
            return "Today";
        } else if (inputDay == today - DAY_IN_MILLIS) {
            return "Yesterday";
        } else {
            return simpleDateFormat.format(new Date(seconds * SECOND_IN_MILLIS));
        }
    }

    public static String toTodayYesterdayFullMonthDate(long seconds) {
        if(seconds > 10000000000L)
            seconds = seconds/1000;
        return toTodayYesterdayDateByFormat(seconds, DAY_AND_MONTH_AND_YEAR_FULL_FORMAT);
    }

    public static String toTodayTomorrowFullMonthDate(long seconds) {
        if(seconds > 10000000000L)
            seconds = seconds/1000;
        return toTodayYesterdayDateByFormat(seconds, DAY_AND_MONTH_AND_YEAR_FORMAT);
    }

    public static long toShortDateLong(long seconds) {
        Calendar calendar = getCalendar(seconds);
        return Long.parseLong(SHORT_DATE_WITHOUT_DIVIDERS_FORMAT.format(calendar.getTime()));
    }

    public static String formatDateSimpleTime(long seconds) {
        return SIMPLE_TIME_FORMAT.format(new Date(seconds * SECOND_IN_MILLIS));
    }

    public static String toTimeYesterdayFullMonthDate(long seconds) {
        return toTimeYesterdayDateByFormat(seconds, DAY_AND_MONTH_AND_YEAR_FULL_FORMAT);
    }

    public static String toTimeYesterdayMonthDate(long seconds) {
        return toTimeYesterdayDateByFormat(seconds, DAY_AND_MONTH_AND_YEAR_FORMAT);
    }

    private static String toTimeYesterdayDateByFormat(long seconds, SimpleDateFormat simpleDateFormat) {
        long today = roundToDays(nowSeconds());
        long inputDay = roundToDays(seconds);

        if (inputDay == today) {
            return formatDateSimpleTime(seconds);
        } else if (inputDay == today - DAY_IN_MILLIS) {
            return "Yesterday";
        } else {
            return simpleDateFormat.format(new Date(seconds * SECOND_IN_MILLIS));
        }
    }

    public static boolean isToday(Date date) {
        return isSameDay(date, Calendar.getInstance().getTime());
    }

    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) {
            throw new IllegalArgumentException("The dates must not be null");
        }
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

}
