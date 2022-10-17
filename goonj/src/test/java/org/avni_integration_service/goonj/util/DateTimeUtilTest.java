package org.avni_integration_service.goonj.util;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeUtilTest {

    private static LocalDateTime REFERENCE_DATE = LocalDateTime.of(2021, 4, 1, 0, 0);
    @Test
    public void formatDateTime() {
        Instant instant = REFERENCE_DATE.toInstant(ZoneOffset.ofHours(0));
        Date date = Date.from(instant);
        if(date.getTimezoneOffset() == -330) {
            date = DateTimeUtil.offsetTimeZone(date, DateTimeUtil.IST, DateTimeUtil.UTC);
        }
        assertEquals("2021-04-01T00:00:00", DateTimeUtil.formatDateTime(date));
    }
}
