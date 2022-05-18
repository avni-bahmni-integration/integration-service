package org.avni_integration_service.avni;

import org.avni_integration_service.avni.client.AvniHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {AvniHttpClient.class})
public class AvniModuleSpringTest extends BaseAvniSpringTest {
    @Autowired
    private AvniHttpClient dummyBean;

    @Test
    void contextLoads() {
    }
}
