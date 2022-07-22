package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.BaseGoonjSpringTest;
import org.avni_integration_service.goonj.dto.DemandsResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@SpringBootTest(classes = {DemandRepository.class})
@Disabled
public class DemandDTORepositoryExternalTest extends BaseGoonjSpringTest {
    @Autowired
    private DemandRepository demandRepositoryGoonj;

    @Test
    public void demandDownload() {
        Instant instant = LocalDateTime.of(2021, 4, 1, 0, 0).toInstant(ZoneOffset.UTC);
        DemandsResponseDTO demands = demandRepositoryGoonj.getDemands(Date.from(instant));
        Assertions.assertNotNull(demands);
        Assertions.assertNotEquals(0, demands.getDemands().length);
        Assertions.assertNotNull(demands.getDemands()[0].get("DemandId"));
        Assertions.assertNotEquals("", demands.getDemands()[0].get("DemandId"));
    }
}
