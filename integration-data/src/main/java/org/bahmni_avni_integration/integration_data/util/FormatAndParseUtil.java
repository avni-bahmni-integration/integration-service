package org.bahmni_avni_integration.integration_data.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class FormatAndParseUtil {
    public static String escapedForSql(String s) {
        return s.replaceAll("'", "''");
    }

    public static String toISODateStringWithTimezone(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        return dateFormat.format(date);
    }

    public static String toISODateString(Date date) {
        DateFormat dateFormat = getIsoDateFormat();
        return String.format("%s%s", dateFormat.format(date), "Z");
    }

    private static DateFormat getIsoDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
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

    public static Date fromIsoDateString(String date) {
        try {
            DateFormat dateFormat = getIsoDateFormat();
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
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
}