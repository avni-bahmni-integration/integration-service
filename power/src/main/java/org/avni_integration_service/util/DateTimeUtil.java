package org.avni_integration_service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);

    public static String formatDateTime(Date localDateTime) {
        return simpleDateFormat.format(localDateTime);
    }

    public static Date convertToDate(String localDateTime) {
        try {
            return simpleDateFormat.parse(localDateTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCurrentDateStringInIST() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        return simpleDateFormat.format(new Date());
    }

    public static Date addTimeToJavaUtilDate(Date date, int units, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, units);
        return calendar.getTime();
    }
}
