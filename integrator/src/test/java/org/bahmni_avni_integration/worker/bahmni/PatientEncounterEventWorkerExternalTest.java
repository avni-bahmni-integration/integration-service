package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.BaseExternalTest;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.PatientEncounterEventWorker;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.PatientEventWorker;
import org.junit.jupiter.api.BeforeAll;
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

    @BeforeAll
    public void beforeAll() {
        patientEventWorker.setConstants(getConstants());

        patientEncounterEventWorker.setConstants(getConstants());
        patientEncounterEventWorker.setMetaData(mappingMetaDataService.getForBahmniEncounterToAvniEncounter());
    }

    @Test
    public void processEncounter() {
        patientEventWorker.process(patientEvent("ec19b096-5c20-4f67-97f8-c16a215b097a"));
        patientEncounterEventWorker.process(encounterEvent("49a677be-53e4-4017-aff2-55900e84e69e"));
    }

    @Test
    public void processLabEncounter() {
        patientEventWorker.process(patientEvent("25e6c02f-2d9f-45bf-bf80-bd7d032fc42f"));
        patientEncounterEventWorker.process(encounterEvent("695a0121-770a-4328-967d-1709ec45bed2"));
    }
}