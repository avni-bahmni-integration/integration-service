package org.avni_integration_service.goonj.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeUtilTest {
    @Test
    public void format() {
        assertEquals("2021-04-01T00:00:00", DateTimeUtil.formatDateTime(LocalDateTime.of(2021, 4, 1, 0, 0)));
    }
}
