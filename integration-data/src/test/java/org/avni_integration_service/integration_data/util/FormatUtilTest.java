package org.avni_integration_service.integration_data.util;

import org.avni_integration_service.util.FormatAndParseUtil;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.*;

public class FormatUtilTest {
    @Test
    public void toISODateString() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(2020, Calendar.MARCH, 3);
        assertEquals("2020-03-03T00:00:00.000Z", FormatAndParseUtil.toISODateTimeString(gregorianCalendar.getTime()));
    }

    @Test
    public void fromAvniDate() {
        Date date = FormatAndParseUtil.fromAvniDate("2020-05-25");
        assertNotNull(date);
    }

    @Test
    public void fromAvniDateTime() {
        Date date = FormatAndParseUtil.fromAvniDateTime("2021-01-14T08:11:29.012Z");
        assertNotNull(date);
    }
}
