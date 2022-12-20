package org.avni_integration_service.bahmni.repository.openmrs;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.avni_integration_service.bahmni.contract.OpenMRSConcept;
import org.avni_integration_service.bahmni.repository.OpenMRSConceptRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Disabled
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
