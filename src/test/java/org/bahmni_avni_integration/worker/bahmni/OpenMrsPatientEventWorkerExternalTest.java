package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.domain.Constants;
import org.bahmni_avni_integration.repository.ConstantsRepository;
import org.bahmni_avni_integration.worker.bahmni.atomfeedworker.OpenMrsPatientEventWorker;
import org.ict4h.atomfeed.client.domain.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpenMrsPatientEventWorkerExternalTest {
    @Autowired
    private OpenMrsPatientEventWorker openMrsPatientEventWorker;
    @Autowired
    ConstantsRepository constantsRepository;

    @Test
    void process() {
        Constants constants = constantsRepository.findAllConstants();
        openMrsPatientEventWorker.setConstants(constants);
        openMrsPatientEventWorker.process(new Event("0", "/openmrs/ws/rest/v1/patient/42baa8d4-145d-4d76-b4de-6aad19cb3f2a?v=full"));
    }
}