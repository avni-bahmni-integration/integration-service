package org.avni_integration_service.integration_data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = IgnoredIntegratingConceptRepository.class)
public class IgnoredIntegratingConceptRepositoryTest extends AbstractSpringTest {
    @Autowired
    private IgnoredIntegratingConceptRepository repository;

    @Test
    public void findAllByErrorRecordLogsErrorTypeIn() {
        repository.findAll();
    }
}
