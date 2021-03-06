package org.bahmni_avni_integration.integration_data.repository.openmrs;

import org.bahmni_avni_integration.contract.bahmni.OpenMRSEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenMRSEntityRepositoryExternalTest {
    @Autowired
    private OpenMRSEntityRepository repository;

    @Test
    void get() {
        OpenMRSEntity encounterType = repository.get("encountertype", "18c9a1d6-c4f5-4b64-8dbb-cf8ded8b9552");
        assertNotNull(encounterType);
    }
}