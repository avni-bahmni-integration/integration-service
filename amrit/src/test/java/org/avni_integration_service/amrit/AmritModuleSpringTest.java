package org.avni_integration_service.amrit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {AmritIntegrationService.class})
public class AmritModuleSpringTest extends BaseAmritSpringTest {
    @Autowired
    private AmritIntegrationService dummyBean;

    @Test
    public void contextLoads() {
    }
}
