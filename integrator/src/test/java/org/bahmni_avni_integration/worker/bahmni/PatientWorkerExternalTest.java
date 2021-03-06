package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PatientWorkerExternalTest {
    @Autowired
    private PatientWorker patientWorker;
    @Autowired
    private ConstantsRepository constantsRepository;

    @Test
    @Disabled("Useful when giving full run")
    public void processPatients() {
        patientWorker.processPatients(constantsRepository.findAllConstants());
    }
}