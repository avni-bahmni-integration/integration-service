package org.bahmni_avni_integration.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtil {
    public static String toISODateString(Date date) {
        DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return String.format("%s%s", df1.format(date), "Z");
    }
}