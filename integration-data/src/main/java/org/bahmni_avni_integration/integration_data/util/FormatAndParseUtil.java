package org.bahmni_avni_integration.integration_data.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class FormatAndParseUtil {
    private static final SimpleDateFormat humanReadableFormat = new SimpleDateFormat("dd-MM-yyyy");;
    private static final SimpleDateFormat isoDateWithTimezone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
    private static final SimpleDateFormat avniDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static String escapedForSql(String s) {
        return s.replaceAll("'", "''");
    }

    public static String toISODateStringWithTimezone(Date date) {
        return isoDateWithTimezone.format(date);
    }

    public static String toISODateString(Date date) {
        return String.format("%s%s", isoDateFormat.format(date), "Z");
    }

    public static String now() {
        return toISODateStringWithTimezone(new Date());
    }

    public static Date fromAvniDate(String dateString) {
        try {
            return avniDateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date fromAvniDateTime(String dateString) {
        try {
            return isoDateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date fromIsoDateString(String date) {
        try {
            return isoDateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date addSeconds(Date date, int seconds) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, seconds);
        return c.getTime();
    }

    public static String fromAvniToOpenMRSDate(String dateString) {
        return FormatAndParseUtil.toISODateString(FormatAndParseUtil.fromAvniDate(dateString));
    }

    public static String fromAvniToOpenMRSGender(String gender) {
        if (Objects.equals(gender, "Male")) return "M";
        if (Objects.equals(gender, "Female")) return "F";
        if (Objects.equals(gender, "Other")) return "U";
        return null;
    }

    public static String toHumanReadableFormat(Date date) {
        return humanReadableFormat.format(date);
    }
}