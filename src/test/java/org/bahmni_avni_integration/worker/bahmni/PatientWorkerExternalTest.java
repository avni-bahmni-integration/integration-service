package org.bahmni_avni_integration.worker.bahmni;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PatientWorkerExternalTest {
    @Autowired
    private PatientWorker patientWorker;

    @Test
    public void processPatients() {
        patientWorker.processPatients();
    }
}