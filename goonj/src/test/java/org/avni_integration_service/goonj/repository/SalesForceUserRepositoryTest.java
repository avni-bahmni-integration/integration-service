package org.avni_integration_service.goonj.repository;

import org.avni_integration_service.goonj.BaseGoonjSpringTest;
import org.avni_integration_service.goonj.domain.AuthResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {SalesForceUserRepository.class})
@Disabled
class SalesForceUserRepositoryTest extends BaseGoonjSpringTest {
    @Autowired
    private SalesForceUserRepository salesForceUserRepository;

    @Test
    public void login() {
        AuthResponse authResponse = salesForceUserRepository.login();
        System.out.printf(authResponse.toString());
    }
}
