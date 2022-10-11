package org.avni_integration_service.goonj.worker;

import org.avni_integration_service.goonj.BaseGoonjSpringTest;
import org.avni_integration_service.goonj.worker.goonj.DemandWorker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {DemandWorker.class})
@Disabled
public class DemandWorkerTest extends BaseGoonjSpringTest {
    @Autowired
    private DemandWorker demandWorker;

    @Test
    public void process() {
        try {
            demandWorker.process();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
