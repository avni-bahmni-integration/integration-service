package org.bahmni_avni_integration;

import org.bahmni_avni_integration.domain.Constants;
import org.bahmni_avni_integration.repository.ConstantsRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseExternalTest {
    @Autowired
    private ConstantsRepository constantsRepository;

    protected Constants getConstants() {
        return constantsRepository.findAllConstants();
    }
}