package org.avni_integration_service.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectJsonMapperTest {
    @Test
    public void serialiseObject() {
        String json = ObjectJsonMapper.writeValueAsString(new Empty());
        assertEquals("{}", json);
    }
}
