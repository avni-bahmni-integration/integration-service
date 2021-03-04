package org.bahmni_avni_integration;

import org.bahmni_avni_integration.domain.Constants;
import org.bahmni_avni_integration.repository.ConstantsRepository;
import org.ict4h.atomfeed.client.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseExternalTest {
    @Autowired
    private ConstantsRepository constantsRepository;

    protected Constants getConstants() {
        return constantsRepository.findAllConstants();
    }

    protected Event patientEvent(String uuid) {
        return new Event("0", String.format("/openmrs/ws/rest/v1/patient/%s?v=full", uuid));
    }

    protected Event encounterEvent(String uuid) {
        return new Event("0", String.format("/openmrs/ws/rest/v1/encounter/%s?v=full", uuid));
    }
}