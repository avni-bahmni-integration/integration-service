package org.avni_integration_service.goonj;

import org.avni_integration_service.goonj.config.GoonjAvniSessionFactory;
import org.avni_integration_service.goonj.config.GoonjContextProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {GoonjContextProvider.class, GoonjAvniSessionFactory.class})
public class GoonjModuleSpringTest extends BaseGoonjSpringTest {
    @Autowired
    private GoonjContextProvider goonjContextProvider;

    @Test
    public void goonjContextLoads() {
    }
}
