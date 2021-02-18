package org.bahmni_avni_integration.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class FormatAndParseUtil {
    public static String toISODateStringWithTimezone(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        return dateFormat.format(date);
    }

    public static String toISODateString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return String.format("%s%s", dateFormat.format(date), "Z");
    }

    public static String now() {
        return toISODateStringWithTimezone(new Date());
    }

    public static Date fromAvniDate(String dateString) {
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df1.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date fromAvniDateTime(String dateString) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String fromAvniToOpenMRSDate(String dateString) {
        return FormatAndParseUtil.toISODateString(FormatAndParseUtil.fromAvniDate(dateString));
    }
}