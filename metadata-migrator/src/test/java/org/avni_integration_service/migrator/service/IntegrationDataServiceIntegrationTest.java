package org.avni_integration_service.migrator.service;

import org.avni_integration_service.integration_data.domain.Constant;
import org.avni_integration_service.integration_data.domain.ConstantKey;
import org.avni_integration_service.integration_data.domain.Constants;
import org.avni_integration_service.integration_data.repository.ConstantsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IntegrationDataServiceIntegrationTest {
    @Autowired
    private IntegrationDataService integrationDataService;
    @Autowired
    private ConstantsRepository constantsRepository;

    @Test
    public void createConstants() {
        constantsRepository.deleteAll();
        integrationDataService.createConstants();
        Constants allConstants = constantsRepository.findAllConstants();
        List<Constant> values = allConstants.getValues(ConstantKey.OutpatientVisitTypes);
        assertEquals(4, values.size());
    }
}
