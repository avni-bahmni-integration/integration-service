package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.BaseGoonjSpringTest;
import org.avni_integration_service.goonj.dto.DispatchesResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;

@SpringBootTest(classes = {DispatchRepository.class})
@Disabled
public class DispatchRepositoryExternalTest extends BaseGoonjSpringTest {
    @Autowired
    private DispatchRepository dispatchRepository;

    @Test
    public void dispatchDownload() {
        Instant instant = LocalDateTime.of(2021, 4, 1, 0, 0).toInstant(ZoneOffset.UTC);
        DispatchesResponseDTO dispatchesResponse = dispatchRepository.getDispatches(Date.from(instant));
        Assertions.assertNotNull(dispatchesResponse);
        Assertions.assertNotEquals(0, dispatchesResponse.getDispatchStatuses().length);
        HashMap<String, Object>[] dispatches = dispatchesResponse.getDispatchStatuses();
        Assertions.assertNotEquals("", dispatches[0].get("DispatchStatusId"));
        Assertions.assertNotNull(dispatches[0].get("DispatchStatusId"));
    }
}
