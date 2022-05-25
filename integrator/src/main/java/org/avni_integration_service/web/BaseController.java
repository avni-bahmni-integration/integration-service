package org.avni_integration_service.web;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.User;
import org.avni_integration_service.integration_data.repository.UserRepository;

import java.security.Principal;

public abstract class BaseController {
    private final UserRepository userRepository;

    public BaseController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected IntegrationSystem getCurrentIntegrationSystem(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        return user.getWorkingIntegrationSystem();
    }
}
