package org.avni_integration_service.goonj.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtil {
    private static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat goonjDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private static final SimpleDateFormat goonjRequestDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatDateTime(LocalDateTime localDateTime) {
        return dateTimeFormat.format(localDateTime);
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

    public static String formatDate(Date encounterDateTime) {
        return goonjRequestDateFormat.format(encounterDateTime);
    }
}
