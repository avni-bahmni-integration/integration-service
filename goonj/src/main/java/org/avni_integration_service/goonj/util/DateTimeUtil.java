package org.avni_integration_service.goonj.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static String formatDateTime(LocalDateTime localDateTime) {
        return dateTimeFormat.format(localDateTime);
    }
}
