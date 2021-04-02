package org.bahmni_avni_integration.integration.data.repository;

import org.bahmni_avni_integration.integration_data.repository.ErrorRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

@SpringBootTest(classes = ErrorRecordRepository.class)
public class ErrorRecordRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private ErrorRecordRepository errorRecordRepository;

    @Test
    public void findAllByErrorRecordLogsErrorTypeIn() {
        errorRecordRepository.findAllByErrorRecordLogsErrorTypeIn(Collections.emptyList(), PageRequest.of(0, 20));
    }
}