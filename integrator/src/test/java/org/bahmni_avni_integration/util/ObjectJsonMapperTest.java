package org.bahmni_avni_integration.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectJsonMapperTest {
    @Test
    public void serialiseObject() {
        String json = ObjectJsonMapper.writeValueAsString(new Empty());
        assertEquals("{}", json);
    }
}