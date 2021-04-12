package org.bahmni_avni_integration.integration.data.repository;

import org.bahmni_avni_integration.integration_data.domain.ErrorType;
import org.bahmni_avni_integration.integration_data.repository.ErrorRecordRepository;
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
        errorRecordRepository.findAllByErrorRecordLogsErrorTypeNotInOrderById(ErrorType.getUnprocessableErrorTypes(), PageRequest.of(1, 20));
    }
}