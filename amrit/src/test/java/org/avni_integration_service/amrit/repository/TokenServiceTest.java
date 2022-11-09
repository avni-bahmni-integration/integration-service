package org.avni_integration_service.amrit.repository;

import org.avni_integration_service.amrit.BaseAmritSpringTest;
import org.avni_integration_service.amrit.service.AmritTokenService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
@SpringBootTest(classes = {AmritTokenService.class})
class TokenServiceTest extends BaseAmritSpringTest {
    @Autowired
    private AmritTokenService tokenService;

    @Test
    public void login() {
        String token = tokenService.getRefreshedToken();
        assertNotNull(token);
    }
}
