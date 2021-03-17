package org.bahmni_avni_integration.migrator.service;

import org.bahmni_avni_integration.migrator.domain.OpenMRSForm;
import org.bahmni_avni_integration.migrator.repository.ImplementationConfigurationRepository;
import org.bahmni_avni_integration.migrator.repository.OpenMRSRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenMRSRepositoryTest {
    @Autowired
    private OpenMRSRepository openMRSRepository;
    @Autowired
    private ImplementationConfigurationRepository implementationConfigurationRepository;

    @Test
    public void populateForms() {
        List<OpenMRSForm> forms = implementationConfigurationRepository.getForms("bahmni-forms.json");
        openMRSRepository.populateForms(forms);
        assertNotEquals(0, forms.get(0).getConcepts().size());
    }
}