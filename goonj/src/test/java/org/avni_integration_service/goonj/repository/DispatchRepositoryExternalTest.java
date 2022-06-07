package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.BaseGoonjSpringTest;
import org.avni_integration_service.goonj.domain.AuthResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashMap;

@SpringBootTest(classes = {DispatchRepository.class})
@Disabled
public class DispatchRepositoryExternalTest extends BaseGoonjSpringTest {
   @Autowired
    private DispatchRepository dispatchRepository;

    @Test
    public void dispatchDownload() {
        HashMap<String, Object>[] dispatches = dispatchRepository.getDispatches(LocalDateTime.of(2021, 4, 1, 0, 0));
        Assertions.assertNotEquals(0, dispatches.length);

        Assertions.assertNotEquals("", dispatches[0].get("MaterialName"));
        Assertions.assertNotNull(dispatches[0].get("MaterialName"));
    }
}
