package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.BaseExternalTest;
import org.bahmni_avni_integration.service.MappingMetaDataService;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.PatientEncounterEventWorker;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.PatientEventWorker;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    public void beforeEach() {
        patientEventWorker.setConstants(getConstants());

        patientEncounterEventWorker.setConstants(getConstants());
        patientEncounterEventWorker.setMetaData(mappingMetaDataService.getForBahmniEncounterToAvniEntities());
    }

    @Test
    public void processEncounter() {
        patientEventWorker.process(patientEvent("ec19b096-5c20-4f67-97f8-c16a215b097a"));
        patientEncounterEventWorker.process(encounterEvent("49a677be-53e4-4017-aff2-55900e84e69e"));
    }

    @Test
    public void processLabEncounter() {
        patientEventWorker.process(patientEvent("9312db47-73eb-452c-9f0b-800bf0c4cbf4"));
        patientEncounterEventWorker.process(encounterEvent("a605cfe6-92e1-4bee-9f02-bded7ee385a2"));
    }

    @Test
    public void processDrugPrescriptionEncounter() {
        patientEventWorker.process(patientEvent("00052bd1-4e72-45ee-9c8f-b711685aae89"));
        patientEncounterEventWorker.process(encounterEvent("42269eee-4d3f-45df-bdbf-b37af98290f9"));
    }
}