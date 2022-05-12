package org.avni_integration_service.bahmni.worker.bahmni;

import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
import org.avni_integration_service.bahmni.worker.bahmni.atomfeedworker.PatientEventWorker;
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
        openMrsPatientEventWorker.cacheRunImmutables(constants);
        openMrsPatientEventWorker.process(new Event("0", "/openmrs/ws/rest/v1/patient/42baa8d4-145d-4d76-b4de-6aad19cb3f2a?v=full"));
    }

    @Test
    public void duplicateTest() {
        Constants constants = constantsRepository.findAllConstants();
        openMrsPatientEventWorker.cacheRunImmutables(constants);
        openMrsPatientEventWorker.process(new Event("0", "/openmrs/ws/rest/v1/patient/866dea68-7d64-4a5f-bad0-18ee43d1736e?v=full"));
    }
}
