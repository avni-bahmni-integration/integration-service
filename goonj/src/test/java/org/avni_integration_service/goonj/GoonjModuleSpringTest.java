package org.avni_integration_service.goonj;

import org.avni_integration_service.goonj.repository.DispatchRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {DispatchRepository.class})
public class GoonjModuleSpringTest extends BaseGoonjSpringTest {
    @Autowired
    private DispatchRepository dummyBean;

    @Test
    public void contextLoads() {
    }
}
