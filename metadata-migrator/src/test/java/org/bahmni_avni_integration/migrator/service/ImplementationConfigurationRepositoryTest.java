package org.bahmni_avni_integration.migrator.service;

import org.bahmni_avni_integration.migrator.domain.OpenMRSForm;
import org.bahmni_avni_integration.migrator.repository.ImplementationConfigurationRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ImplementationConfigurationRepositoryTest {
    @Test
    public void getForms() {
        ImplementationConfigurationRepository implementationConfigurationRepository = new ImplementationConfigurationRepository();
        List<OpenMRSForm> forms = implementationConfigurationRepository.getForms("bahmni-forms.json");
        assertNotNull(forms);
        assertEquals(11, forms.size());
    }
}