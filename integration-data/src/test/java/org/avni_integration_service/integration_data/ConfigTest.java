package org.avni_integration_service.integration_data;

import org.avni_integration_service.integration_data.repository.AbstractSpringTest;
import org.avni_integration_service.integration_data.config.BahmniConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = BahmniConfig.class)
class ConfigTest extends AbstractSpringTest {
    @Autowired
    private BahmniConfig config;

    @Test
    public void readCustomProperties() {
        assertNotNull(config.getOpenMrsMySqlUser());
    }
}
