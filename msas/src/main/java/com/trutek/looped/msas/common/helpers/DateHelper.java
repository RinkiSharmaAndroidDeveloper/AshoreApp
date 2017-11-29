package com.trutek.looped.msas.common.helpers;


import android.widget.TextView;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateHelper implements JsonSerializer<Date>, JsonDeserializer<Date> {

    private static final String FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String FORMAT_TIME = "hh:mm aa";
    private static final String FORMAT_TIME_24HR = "kk:mm";
    private static final String FORMAT_DATE = "MM/dd/yyyy";
    private static final String FORMAT_DATETIME = "MM-dd-yyyy - hh:mm aa";
    private static final String FORMAT_FULL_MONTH_DATE = "MMMM dd, yyyy";
    private static final String CUSTOM_FORMAT_DATE = "MM/dd/yyyy hh:mm aa";
    public static final String CUSTOM_FORMAT_MONTH = "EEEE MMM, dd, hh:mm aa";
    private static final TimeZone utc = TimeZone.getTimeZone("UTC");

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public static Date getBOD(Date date) {
        GregorianCalendar date1 = new GregorianCalendar();
        date1.setTime(date);
        date1.set(Calendar.HOUR_OF_DAY, -1);
        date1.set(Calendar.MINUTE, 59);
        date1.set(Calendar.SECOND, 59);
        return date1.getTime();
    }

    public static Date getStartOfDay(Date date){
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.getTime();
    }

    public static Date getEOD(Date date) {
        GregorianCalendar date1 = new GregorianCalendar();
        date1.setTime(date);
        date1.add(Calendar.DAY_OF_MONTH, 1);
        date1.set(Calendar.HOUR_OF_DAY, 0);
        date1.set(Calendar.MINUTE, 0);
        date1.set(Calendar.SECOND, 0);
        return date1.getTime();
    }

    public static Date parse(TextView view, StringifyAs stringifyAs) {
        return parse(view.getText().toString(), stringifyAs);
    }

    public static Date parse(TextView view) {
        return parse(view.getText().toString(), StringifyAs.ReadableDate);
    }

    public static Date parse(String stringifyDate) {
        if (stringifyDate.matches("[0-9]+")) {
            long miliseconds = Long.parseLong(stringifyDate);
            Date date = new Date(miliseconds);
            return date;
        } else
            return parse(stringifyDate, StringifyAs.ReadableDate);
    }

    public static Date setDate(Date date, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.set(year, monthOfYear, dayOfMonth);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTime();
    }

    public static Date setTime(Date date, int hourOfDay, int minute) {
        Calendar calendar = GregorianCalendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTime();
    }

    public static Timeline getTimeline(Date dateToCompare) {
        Date date = onlyDate(dateToCompare);

        Date today = getToday();


        if (date.getTime() == today.getTime()) {
            return Timeline.today;
        }

        if (date.after(today)) {
            if (date.after(getDayAfter(1))) {
                return Timeline.upcomming;
            } else {
                return Timeline.tomorrow;
            }
        } else {
            return Timeline.older;
        }
    }

    public static Date onlyDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.clear(Calendar.AM_PM);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);

        return calendar.getTime();
    }

    public static Date parse(String stringifyDate, StringifyAs stringifyAs) {
        if (stringifyAs == null || stringifyAs.equals(""))
            return null;

        Date date = null;
        DateFormat dateFormat;
        switch (stringifyAs) {
            case Readable:
                dateFormat = new SimpleDateFormat(FORMAT_DATETIME);
                break;
            case ReadableDate:
                dateFormat = new SimpleDateFormat(FORMAT_DATE);
                break;
            case ReadableTime:
                dateFormat = new SimpleDateFormat(FORMAT_TIME);
                break;

            case Time24Hr:
                dateFormat = new SimpleDateFormat(FORMAT_TIME_24HR);
                break;

            case Custom_format:
                dateFormat = new SimpleDateFormat(CUSTOM_FORMAT_DATE);
                break;

            case Utc:
                dateFormat = new SimpleDateFormat(FORMAT_ISO);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                break;

            case Since1Jan1970:
                return new Date(Long.parseLong(stringifyDate));

            default:
                dateFormat = new SimpleDateFormat("MMMM dd, yyyy - hh:mm aa");
                break;
        }

        try {
            date = dateFormat.parse(stringifyDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getDayBefore(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -days);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.AM_PM);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTime();

    }

    public static Date getDayBefore(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -days);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.AM_PM);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTime();

    }

    public static Date getDayAfter(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.AM_PM);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTime();

    }

    public static Date getDayAfter(int days , Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(calendar.DATE,days);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.AM_PM);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTime();

    }

    public static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.clear(Calendar.AM_PM);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTime();

    }

    public static String stringify(Date date) {
        return stringify(date, StringifyAs.ReadableDate);
    }

    public static String stringifyTime(Date date) {
        return stringify(date, StringifyAs.ReadableTime);
    }

    public static String stringify(Date date, StringifyAs stringifyAs) {
        if (date == null)
            return null;
        switch (stringifyAs) {
            case ReadableDate:
                return (new SimpleDateFormat(FORMAT_DATE)).format(date);

            case Readable:
                return (new SimpleDateFormat(FORMAT_DATETIME)).format(date);

            case ReadableTime:
                return (new SimpleDateFormat(FORMAT_TIME)).format(date);

            case Time24Hr:
                return (new SimpleDateFormat(FORMAT_TIME_24HR)).format(date);

            case FullMonthDate:
                return (new SimpleDateFormat(FORMAT_FULL_MONTH_DATE)).format(date);

            case Since1Jan1970:
                Long since1Jan1970date = date.getTime();
                return since1Jan1970date.toString();

            case Utc:
                SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_ISO);
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                return dateFormat.format(date);

            default:
                return date.toString();
        }
    }

    public static String stringify(Date date, String format) {
        return (new SimpleDateFormat(format)).format(date);
    }

    public static Long milliSeconds(String date, String time) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATETIME);
        Date date1 = new Date();
        try {
            date1 = sdf.parse(date + " " + time);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return date1.getTime();
    }

    public static Date merge(Date date, Date time) {
        return new Date(date.getYear(), date.getMonth(), date.getDate(), time.getHours(), time.getMinutes(), time.getSeconds());
    }

    public static Long diffDays(Date fromDate, Date tillDate) {

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(fromDate.getYear(), fromDate.getMonth(), fromDate.getDate());
        date2.clear();
        date2.set(tillDate.getYear(), tillDate.getMonth(), tillDate.getDate());

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        return diff / (24 * 60 * 60 * 1000);
    }

    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(stringify(date, StringifyAs.Utc));
    }

    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return parse(jsonElement.getAsString(), StringifyAs.Utc);
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = (new Date()).getTime();
        if (time > now || time <= 0) {
            return null;
        }

        long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return stringify(new Date(time), StringifyAs.Time24Hr);
//            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return stringify(new Date(time), StringifyAs.Time24Hr);
//            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return stringify(new Date(time), StringifyAs.Time24Hr);
//            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return stringify(new Date(time), StringifyAs.Time24Hr);
//            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return stringify(new Date(time), StringifyAs.Time24Hr);
//            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return stringify(new Date(time));
        }
    }

    public enum Timeline {
        older,
        today,
        tomorrow,
        upcomming
    }

    public enum StringifyAs {
        Readable,
        ReadableDate,
        ReadableTime,
        FullMonthDate,
        Time24Hr,
        Utc,
        Since1Jan1970,
        Custom_format
    }

    public static String getAgoTime(String sDate){
        Date _todayDate = new Date();
        Date _notificationDate = null;
        _notificationDate = DateHelper.parse(sDate, DateHelper.StringifyAs.Utc);
        _todayDate = DateHelper.parse(DateHelper.stringify(_todayDate, DateHelper.StringifyAs.Utc), DateHelper.StringifyAs.Utc);

        Long diff = _todayDate.getTime() - _notificationDate.getTime();

        Long _seconds, _min, _hour, _days;

        _seconds = TimeUnit.MILLISECONDS.toSeconds(diff);
        if (_seconds > 59) {
            _min = TimeUnit.MILLISECONDS.toMinutes(diff);
            if (_min > 59) {
                _hour = TimeUnit.MILLISECONDS.toHours(diff);
                if (_hour > 23) {
                    _days = TimeUnit.MILLISECONDS.toDays(diff);
                    if (_days > 2) {
                        return DateHelper.stringify(_notificationDate, "MM-dd-yyyy");
                    } else {
                        if (_days == 1) {
                            return _days + " day ago";
                        } else {
                            return _days + " days ago";
                        }
                    }
                } else {
                    if (_hour == 1) {
                        return _hour + " hour ago";
                    } else {
                        return _hour + " hours ago";
                    }
                }
            } else {
                if (_min == 1) {
                    return _min.toString() + " min ago";
                } else {
                    return _min.toString() + " mins ago";
                }
            }
        } else {
            if (_seconds < 0) {
                return "0 sec ago";
            } else if (_seconds == 1) {
                return _seconds.toString() + " sec ago";
            } else {
                return _seconds.toString() + " secs ago";
            }
        }
    }

    public static Date getFirstDayOfMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    public static Date getFirstDayOfMonth(){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }
    public static Date getLastDayOfMonth(Date date){
        Calendar c = Calendar.getInstance();
        c.clear(Calendar.MINUTE);
        c.clear(Calendar.SECOND);
        c.clear(Calendar.MILLISECOND);
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH , c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    public static Date getFirstDayOfLastMonth(){
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.add(Calendar.MONTH, -1);
        aCalendar.set(Calendar.DATE, 1);
        return aCalendar.getTime();
    }

    public static Date getAgoDate(Date date, int days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);
        return cal.getTime();
    }

    public static Date getAgoDate(Date date,int hours,int minutes){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, -hours);
        cal.add(Calendar.MINUTE,-minutes);
        return cal.getTime();
    }

    public static int getCurrentDay(){
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static int getCurrentDay(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    public static int getCurrentMonth(){
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH);
    }

    public static int getMonth(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH);
    }

    public static float calculateHoursForGraph(Date from, Date to){
        long startTime, endTime;
        startTime = from.getTime();
        endTime = to.getTime();

        return ((float)(endTime-startTime)/(60 * 60 * 1000));
    }
    public static Date getFirstDayOfYear(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH,Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        return calendar.getTime();
    }

    public static int noOfDaysOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int noOfDaysOfMonth(int monthNo){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, monthNo);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Date getFirstDateOfWeek(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return  c.getTime();
    }

    public static int getMonthIntFromDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static int getLastMonth(){
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) - 1;
    }

    public static Date getFirstDayOfCurrentMonth(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH,Calendar.MONTH);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        return calendar.getTime();
    }

    public static Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date convertToDate(int hour, int minutes){
        final String time = hour +":"+minutes;
        Date date = null;

        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_TIME_24HR);
            date = sdf.parse(time);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

}
