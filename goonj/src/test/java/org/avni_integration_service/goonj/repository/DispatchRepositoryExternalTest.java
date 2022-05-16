package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.BaseGoonjSpringTest;
import org.avni_integration_service.goonj.domain.AuthResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest(classes = {SalesForceUserRepository.class, DemandRepository.class})
@Disabled
public class DispatchRepositoryExternalTest extends BaseGoonjSpringTest {
    @Autowired
    private SalesForceUserRepository salesForceUserRepository;
    @Autowired
    private DemandRepository demandRepository;

    @Test
    public void dispatchDownload() {
        AuthResponse authResponse = salesForceUserRepository.login();
        demandRepository.getDemands(authResponse, LocalDateTime.of(2021, 4, 1, 0, 0));
    }
}
