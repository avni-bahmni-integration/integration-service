package org.avni_integration_service.goonj.util;

import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

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
    private static final Jsr310JpaConverters.LocalDateTimeConverter ldtc = new Jsr310JpaConverters.LocalDateTimeConverter();


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
}
