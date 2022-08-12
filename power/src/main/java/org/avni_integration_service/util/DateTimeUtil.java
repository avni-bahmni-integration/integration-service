package org.avni_integration_service.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
}
