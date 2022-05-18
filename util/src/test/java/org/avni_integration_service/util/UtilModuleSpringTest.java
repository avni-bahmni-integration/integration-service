package org.avni_integration_service.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = HealthCheckService.class)
public class UtilModuleSpringTest extends BaseUtilSpringTest {
    @Autowired
    private HealthCheckService dummyBean;

    @Test
    public void contextLoads() {
    }
}
