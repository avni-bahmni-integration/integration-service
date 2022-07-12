package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.BaseGoonjSpringTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashMap;

@SpringBootTest(classes = {DemandRepository.class})
@Disabled
public class DemandRepositoryExternalTest extends BaseGoonjSpringTest {
    @Autowired
    private DemandRepository demandRepositoryGoonj;

    @Test
    public void demandDownload() {
        HashMap<String, Object>[] demands = demandRepositoryGoonj.getDemands(LocalDateTime.of(2021, 4, 1, 0, 0));
        Assertions.assertNotEquals(0, demands.length);
    }
}
