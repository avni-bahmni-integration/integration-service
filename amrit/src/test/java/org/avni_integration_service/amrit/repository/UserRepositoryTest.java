package org.avni_integration_service.amrit.repository;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Disabled
class UserRepositoryTest {
    @Autowired
    private AmritUserRepository userRepository;

    @Test
    public void login() {
        userRepository.login();
    }
}
