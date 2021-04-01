package org.bahmni_avni_integration.contract.bahmni;

import org.bahmni_avni_integration.util.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OpenMRSPatientTest {
    @Test
    public void deserializePatient() {
        OpenMRSPatient patient = TestUtils.readResource("fullPatient.json", OpenMRSPatient.class);
        assertFalse(patient.isVoided());
    }
}