package org.bahmni_avni_integration.repository.openmrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSConcept;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OpenMRSConceptRepositoryExternalTest {
    @Autowired
    private OpenMRSConceptRepository conceptRepository;

    @Test
    public void getConcept() throws JsonProcessingException {
        OpenMRSConcept openMRSConcept = conceptRepository.getConceptByName("Departments, General OPD");
        assertNotNull(openMRSConcept);
        assertNotNull(openMRSConcept.getUuid());
    }
}