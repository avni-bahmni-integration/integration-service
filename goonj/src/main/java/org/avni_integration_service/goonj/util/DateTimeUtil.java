package org.avni_integration_service.goonj.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class DateTimeUtil {
    private static final DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static String formatDateTime(LocalDateTime localDateTime) {
        return dateTimeFormat.format(localDateTime);
    }
}
