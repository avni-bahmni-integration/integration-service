package org.bahmni_avni_integration.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtil {
    public static String toISODateString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return String.format("%s%s", dateFormat.format(date), "Z");
    }

    public static Date fromAvniDate(String dateString) {
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return df1.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}