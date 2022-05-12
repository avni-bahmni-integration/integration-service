package org.avni_integration_service.integration_data.repository.openmrs;

import org.avni_integration_service.bahmni.repository.BaseOpenMRSRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseOpenMRSRepositoryTest {
    @Test
    public void transformEncounterContent() {
        assertEquals("/openmrs/ws/rest/v1/encounter/bd4dcb1d-0d2e-4cb4-8f30-70d1b7690122?v=full", BaseOpenMRSRepository.transformEncounterContent("/openmrs/ws/rest/v1/bahmnicore/bahmniencounter/bd4dcb1d-0d2e-4cb4-8f30-70d1b7690122?includeAll=true"));
        String notToBeModified = "/openmrs/ws/rest/v1/patient/bd4dcb1d-0d2e-4cb4-8f30-70d1b7690122?v=full";
        assertEquals(notToBeModified, BaseOpenMRSRepository.transformEncounterContent(notToBeModified));
    }
}
