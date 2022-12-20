package org.avni_integration_service.goonj.util;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtil {

    public static String IST = "Asia/Kolkata";
    public static String UTC = "UTC";
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat goonjDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat goonjRequestDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final Jsr310JpaConverters.LocalDateTimeConverter ldtc = new Jsr310JpaConverters.LocalDateTimeConverter();


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

    public static Date convertToDateFromGoonjDateString(String goonjDateString) {
        try {
            return goonjDateFormat.parse(goonjDateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatDateTime(String goonjDateString) {
        try {
            Date date = goonjDateFormat.parse(goonjDateString);
            return dateTimeFormat.format(ldtc.convertToEntityAttribute(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatDate(Date encounterDateTime) {
        return goonjRequestDateFormat.format(encounterDateTime);
    }

    public static Date offsetTimeZone(Date date, String fromTZ, String toTZ){

        // Construct FROM and TO TimeZone instances
        TimeZone fromTimeZone = TimeZone.getTimeZone(fromTZ);
        TimeZone toTimeZone = TimeZone.getTimeZone(toTZ);

        // Get a Calendar instance using the default time zone and locale.
        Calendar calendar = Calendar.getInstance();

        // Set the calendar's time with the given date
        calendar.setTimeZone(fromTimeZone);
        calendar.setTime(date);

        System.out.println("Input: " + calendar.getTime() + " in " + fromTimeZone.getDisplayName());

        // FROM TimeZone to UTC
        calendar.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);

        if (fromTimeZone.inDaylightTime(calendar.getTime())) {
            calendar.add(Calendar.MILLISECOND, calendar.getTimeZone().getDSTSavings() * -1);
        }

        // UTC to TO TimeZone
        calendar.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());

        if (toTimeZone.inDaylightTime(calendar.getTime())) {
            calendar.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
        }

        return calendar.getTime();
    }
}
