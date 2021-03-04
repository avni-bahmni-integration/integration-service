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
        patientEventWorker.process(patientEvent("b789f855-7bc5-4ac1-af84-523d79e103fe"));

        patientEncounterEventWorker.setConstants(getConstants());
        patientEncounterEventWorker.setMetaData(mappingMetaDataService.getForBahmniEncounterToAvniEncounter());
        patientEncounterEventWorker.process(encounterEvent("00a48800-113f-40e3-adc1-07cd4e5a68d8"));
    }
}