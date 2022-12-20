package org.avni_integration_service.bahmni.contract;

import org.avni_integration_service.bahmni.TestUtils;
import org.avni_integration_service.bahmni.contract.OpenMRSPatient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpenMRSPatientTest {
    @Test
    public void deserializePatient() {
        OpenMRSPatient patient = TestUtils.readResource("fullPatient.json", OpenMRSPatient.class);
        assertFalse(patient.isVoided());
    }
}
