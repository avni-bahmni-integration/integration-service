package org.bahmni_avni_integration.service;

import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.domain.ErrorRecord;
import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ErrorServiceTest {
    @Autowired
    private ErrorService errorService;

    @Test
    public void errorOccurred() {
        ErrorRecord errorRecord = errorService.errorOccurred(UUID.randomUUID().toString(), ErrorType.PatientIdChanged, BahmniEntityType.Patient);
        assertNotNull(errorRecord);
    }
}