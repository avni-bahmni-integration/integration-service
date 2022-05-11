package org.avni_integration_service.integration_data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = IgnoredBahmniConceptRepository.class)
public class IgnoredBahmniConceptRepositoryTest extends AbstractSpringTest {
    @Autowired
    private IgnoredBahmniConceptRepository repository;

    @Test
    public void findAllByErrorRecordLogsErrorTypeIn() {
        repository.findAll();
    }
}
