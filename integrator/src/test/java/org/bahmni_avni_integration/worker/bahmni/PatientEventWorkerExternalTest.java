package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.integration_data.domain.Constants;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.PatientEventWorker;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
class PatientEventWorkerExternalTest {
    @Autowired
    private PatientEventWorker openMrsPatientEventWorker;
    @Autowired
    ConstantsRepository constantsRepository;

    @Test
    void process() {
        Constants constants = constantsRepository.findAllConstants();
        openMrsPatientEventWorker.setConstants(constants);
        openMrsPatientEventWorker.process(new Event("0", "/openmrs/ws/rest/v1/patient/42baa8d4-145d-4d76-b4de-6aad19cb3f2a?v=full"));
    }

    @Test
    public void duplicateTest() {
        Constants constants = constantsRepository.findAllConstants();
        openMrsPatientEventWorker.setConstants(constants);
        openMrsPatientEventWorker.process(new Event("0", "/openmrs/ws/rest/v1/patient/00049185-d25f-43eb-b89d-26be84bea700?v=full"));
    }
}