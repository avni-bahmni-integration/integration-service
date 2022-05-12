package org.avni_integration_service.service;

import org.avni_integration_service.bahmni.BahmniEntityType;
import org.avni_integration_service.bahmni.service.ErrorService;
import org.avni_integration_service.integration_data.domain.ErrorRecord;
import org.avni_integration_service.integration_data.domain.ErrorType;
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
