package org.bahmni_avni_integration.mapper;

import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;
import org.bahmni_avni_integration.util.ObjectJsonMapper;

import java.io.IOException;

public class OpenMRSPatientMapper {
    public OpenMRSPatient map(String patientJSON) throws IOException {
        return ObjectJsonMapper.readValue(patientJSON, OpenMRSPatient.class);
    }
}