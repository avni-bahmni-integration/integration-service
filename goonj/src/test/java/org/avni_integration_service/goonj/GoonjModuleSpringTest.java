package org.avni_integration_service.goonj;

import org.avni_integration_service.goonj.config.GoonjConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {GoonjIntegrationService.class, GoonjConfig.class})
public class GoonjModuleSpringTest extends BaseGoonjSpringTest {
    @Autowired
    private GoonjIntegrationService mainBean;
    @Autowired
    private GoonjConfig config;

    @Test
    public void goonjContextLoads() {
        System.out.println(config.getAppUrl());
    }
}
