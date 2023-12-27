package org.bahmni_avni_integration.worker.avni;

import org.bahmni_avni_integration.integration_data.repository.avni.AvniEncounterRepository;
import org.bahmni_avni_integration.integration_data.util.FormatAndParseUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EncounterExternalTest {
    @Autowired
    private AvniEncounterRepository avniEncounterRepository;

    @Test
    public void checkTimeout() {
        avniEncounterRepository.getGeneralEncounters(FormatAndParseUtil.fromIsoDate("2023-11-01"));
    }
}
