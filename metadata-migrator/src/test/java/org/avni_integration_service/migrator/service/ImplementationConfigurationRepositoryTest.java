package org.avni_integration_service.migrator.service;

import org.avni_integration_service.migrator.domain.OpenMRSForm;
import org.avni_integration_service.migrator.repository.ImplementationConfigurationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ImplementationConfigurationRepositoryTest {
    @Autowired
    ImplementationConfigurationRepository implementationConfigurationRepository;

    @Test
    public void getForms() {
        List<OpenMRSForm> forms = implementationConfigurationRepository.getForms();
        assertNotNull(forms);
        assertEquals(11, forms.size());
    }
}
