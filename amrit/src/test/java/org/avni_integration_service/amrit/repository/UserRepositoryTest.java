package org.avni_integration_service.amrit.repository;

import org.avni_integration_service.amrit.BaseAmritSpringTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
@SpringBootTest(classes = {AmritUserRepository.class})
class UserRepositoryTest extends BaseAmritSpringTest {
    @Autowired
    private AmritUserRepository userRepository;

    @Test
    public void login() {
        String token = userRepository.login();
        assertNotNull(token);
    }
}
