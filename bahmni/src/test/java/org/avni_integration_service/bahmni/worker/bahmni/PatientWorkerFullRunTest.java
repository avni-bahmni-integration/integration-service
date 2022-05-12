package org.avni_integration_service.bahmni.worker.bahmni;

import org.avni_integration_service.BaseExternalTest;
import org.avni_integration_service.bahmni.service.MappingMetaDataService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
public class PatientWorkerFullRunTest extends BaseExternalTest {
    @Autowired
    private PatientWorker patientWorker;
    @Autowired
    private PatientEncounterWorker patientEncounterWorker;
    @Autowired
    private MappingMetaDataService mappingMetaDataService;

    @Test
    public void processPatients() {
        patientWorker.cacheRunImmutables(getConstants());
        patientWorker.processPatients();
    }

    @Test
    public void processEncounters() {
        patientEncounterWorker.cacheRunImmutables(getConstants());
        patientEncounterWorker.processEncounters();
    }
}
