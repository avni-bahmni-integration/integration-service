package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.error.ErrorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

@SpringBootTest(classes = ErrorRecordRepository.class)
public class ErrorRecordRepositoryTest extends AbstractSpringTest {
    @Autowired
    private ErrorRecordRepository errorRecordRepository;

    @Test
    public void findAllByErrorRecordLogsErrorTypeIn() {
        errorRecordRepository.findAllByAvniEntityTypeNotNullAndProcessingDisabledFalseAndErrorRecordLogsErrorTypeNotInOrderById(ErrorType.getUnprocessableErrorTypes(), PageRequest.of(1, 20));
    }
}
