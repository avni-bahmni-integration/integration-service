package org.avni_integration_service.goonj.worker;

import org.avni_integration_service.goonj.BaseGoonjSpringTest;
import org.avni_integration_service.goonj.worker.goonj.DemandWorker;
import org.avni_integration_service.goonj.worker.goonj.DispatchWorker;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {DispatchWorker.class})
@Disabled
public class DispatchWorkerTest extends BaseGoonjSpringTest {
    @Autowired
    private DispatchWorker dispatchWorker;

    @Test
    public void process() {
        try {
            dispatchWorker.process();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
