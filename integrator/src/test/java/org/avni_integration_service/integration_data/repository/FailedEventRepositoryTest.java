package org.avni_integration_service.integration_data.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FailedEventRepositoryTest {
    @Autowired
    private FailedEventRepository failedEventRepository;

    @Test
    public void delete() {
        failedEventRepository.deleteAll();
    }
}
