package org.avni_integration_service.bahmni.service;

import org.avni_integration_service.bahmni.BahmniEntityType;
import org.avni_integration_service.bahmni.BahmniErrorType;
import org.avni_integration_service.bahmni.BaseSpringTest;
import org.avni_integration_service.integration_data.domain.error.ErrorRecord;
import org.avni_integration_service.integration_data.repository.ErrorRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {AvniBahmniErrorService.class, ErrorRecordRepository.class})
class ErrorServiceTest extends BaseSpringTest {
    @Autowired
    private AvniBahmniErrorService avniBahmniErrorService;
    @Autowired
    private ErrorRecordRepository errorRecordRepository;

    @Test
    public void errorOccurred() {
        ErrorRecord errorRecord = avniBahmniErrorService.errorOccurred(UUID.randomUUID().toString(), BahmniErrorType.PatientIdChanged, BahmniEntityType.Patient);
        assertNotNull(errorRecord);
    }

    @Test
    public void findAllByErrorRecordLogsErrorTypeIn() {
        errorRecordRepository.findAllByAvniEntityTypeNotNullAndProcessingDisabledFalseAndErrorRecordLogsErrorTypeNotInOrderById(avniBahmniErrorService.getUnprocessableErrorTypes(), PageRequest.of(1, 20));
    }
}
