package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.BaseExternalTest;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PatientWorkerFullRunTest extends BaseExternalTest {
    @Autowired
    private PatientWorker patientWorker;
    @Autowired
    private PatientEncounterWorker patientEncounterWorker;
    @Autowired
    private MappingMetaDataService mappingMetaDataService;

    @Test
    public void processPatients() {
        patientWorker.processPatients(getConstants());
    }

    @Test
    public void processEncounters() {
        patientEncounterWorker.processEncounters(getConstants(), mappingMetaDataService.getForBahmniEncounterToAvniEntities());
    }
}