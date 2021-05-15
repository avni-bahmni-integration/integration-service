package org.bahmni_avni_integration.integration_data.repository.openmrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSUuidHolder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenMRSPatientRepositoryExternalTest {
    @Autowired
    private OpenMRSPatientRepository openMRSPatientRepository;

    @Test
    public void findPatientByIdentifier() throws JsonProcessingException {
        OpenMRSPatient patient = openMRSPatientRepository.getPatientByIdentifier("TRI08121601");
        assertNotNull(patient);
        assertNotNull(patient.getUuid());
    }
}