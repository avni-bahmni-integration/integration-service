package org.avni_integration_service.bahmni.service;

import org.avni_integration_service.bahmni.BahmniEntityType;
import org.avni_integration_service.bahmni.BaseSpringTest;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;
import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {ErrorService.class})
class ErrorServiceTest extends BaseSpringTest {
    @Autowired
    private ErrorService errorService;

    @Test
    public void errorOccurred() {
        ErrorRecord errorRecord = errorService.errorOccurred(UUID.randomUUID().toString(), ErrorType.IntEntityIdChanged, BahmniEntityType.Patient);
        assertNotNull(errorRecord);
    }
}
