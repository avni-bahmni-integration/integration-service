package org.avni_integration_service.worker.bahmni;

import org.avni_integration_service.BaseExternalTest;
import org.avni_integration_service.worker.bahmni.atomfeedworker.PatientEncounterEventWorker;
import org.avni_integration_service.worker.bahmni.atomfeedworker.PatientEventWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
public class PatientEncounterEventWorkerExternalTest extends BaseExternalTest {
    @Autowired
    private PatientEventWorker patientEventWorker;
    @Autowired
    private PatientEncounterEventWorker patientEncounterEventWorker;

    @BeforeEach
    public void beforeEach() {
        patientEventWorker.cacheRunImmutables(getConstants());
        patientEncounterEventWorker.cacheRunImmutables(getConstants());
    }

    @Test
    public void processEncounter() {
        patientEventWorker.process(patientEvent("ec19b096-5c20-4f67-97f8-c16a215b097a"));
        patientEncounterEventWorker.process(encounterEvent("49a677be-53e4-4017-aff2-55900e84e69e"));
    }

    @Test
    public void processEncounterWithCodedDiagnosis() {
//        visit diagnosis
        patientEncounterEventWorker.process(encounterEvent("bc29306a-db5c-417c-9a94-315bd2bbb6d5"));
//        chief complaint
//        patientEncounterEventWorker.process(encounterEvent("dcfbdffa-e99d-4f7d-9dd0-8988e6add305"));
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

    @Test
    public void processProgramEncounter() {
        patientEventWorker.process(patientEvent("999b1bda-e7a2-4601-ab76-79e09a1ef890"));
        patientEncounterEventWorker.process(encounterEvent("30791204-694e-4473-8c9a-7dc8e12cbfba"));
        patientEncounterEventWorker.process(encounterEvent("89dac55e-811f-4334-80c4-6c57f60fa64e"));
        patientEncounterEventWorker.process(encounterEvent("af660d4b-baf5-4a2e-bf4f-3a1cc5348d4e"));
        patientEncounterEventWorker.process(encounterEvent("b4a34014-10a2-42d3-a2db-553cb0153753"));
//        30791204-694e-4473-8c9a-7dc8e12cbfba, 89dac55e-811f-4334-80c4-6c57f60fa64e, af660d4b-baf5-4a2e-bf4f-3a1cc5348d4e, b4a34014-10a2-42d3-a2db-553cb0153753
    }
}
