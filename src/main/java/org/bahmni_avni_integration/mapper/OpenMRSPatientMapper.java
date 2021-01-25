package org.bahmni_avni_integration.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bahmni_avni_integration.contract.bahmni.OpenMRSPatient;

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