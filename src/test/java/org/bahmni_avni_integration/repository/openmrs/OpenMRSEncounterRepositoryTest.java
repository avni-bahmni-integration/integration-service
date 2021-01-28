package org.bahmni_avni_integration.repository.openmrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSEncounter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenMRSEncounterRepositoryTest {
    @Autowired
    OpenMRSEncounterRepository openMRSEncounterRepository;

    @Test
    public void getEncounterByObservation() throws JsonProcessingException {
        OpenMRSEncounter encounter = openMRSEncounterRepository.getEncounter("TRI01099902", "Departments", "Departments, General OPD");
        assertNull(encounter);
    }
}