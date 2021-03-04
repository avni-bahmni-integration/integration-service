package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.BaseExternalTest;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.PatientEncounterEventWorker;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.PatientEventWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PatientEncounterEventWorkerExternalTest extends BaseExternalTest {
    @Autowired
    private PatientEventWorker patientEventWorker;
    @Autowired
    private PatientEncounterEventWorker patientEncounterEventWorker;
    @Autowired
    private MappingMetaDataService mappingMetaDataService;

    @Test
    public void processEncounter() {
        patientEventWorker.setConstants(getConstants());
        patientEventWorker.process(patientEvent("ec19b096-5c20-4f67-97f8-c16a215b097a"));

        patientEncounterEventWorker.setConstants(getConstants());
        patientEncounterEventWorker.setMetaData(mappingMetaDataService.getForBahmniEncounterToAvniEncounter());
        patientEncounterEventWorker.process(encounterEvent("49a677be-53e4-4017-aff2-55900e84e69e"));
    }
}