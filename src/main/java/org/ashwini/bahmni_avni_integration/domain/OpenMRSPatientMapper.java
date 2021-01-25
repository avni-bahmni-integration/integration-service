package org.ashwini.bahmni_avni_integration.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ashwini.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;

import java.io.IOException;

public class OpenMRSPatientMapper {
    private ObjectMapper objectMapper;

    public OpenMRSPatientMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OpenMRSPatient map(String patientJSON) throws IOException {
        return objectMapper.readValue(patientJSON, OpenMRSPatient.class);
    }
}