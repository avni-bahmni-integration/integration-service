package org.bahmni_avni_integration.worker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HealthCheckServiceTest {
    @Autowired
    private HealthCheckService healthCheckService;
    @Value("${healthcheck.abi.test}")
    private String uuid;

    @Test
    public void testHealthCheck() {
        healthCheckService.verify(uuid);
    }
}
