package org.avni_integration_service.contract.avni;

import org.avni_integration_service.util.ObjectJsonMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProgramEncounterTest {

    @Test
    public void testIgnoredMembers() {
        var programEncounter = new ProgramEncounter();
        programEncounter.set("Subject ID", "019201cd-8d54-431c-916e-678444f4730f");
        var json = ObjectJsonMapper.writeValueAsString(programEncounter);
        assertTrue(json.contains("Subject ID"));
        assertFalse(json.contains("subjectId"));
    }
}
