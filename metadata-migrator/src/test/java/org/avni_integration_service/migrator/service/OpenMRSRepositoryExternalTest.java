package org.avni_integration_service.migrator.service;

import org.avni_integration_service.migrator.domain.OpenMRSForm;
import org.avni_integration_service.migrator.repository.ImplementationConfigurationRepository;
import org.avni_integration_service.migrator.repository.OpenMRSRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Disabled
class OpenMRSRepositoryExternalTest {
    @Autowired
    private OpenMRSRepository openMRSRepository;
    @Autowired
    private ImplementationConfigurationRepository implementationConfigurationRepository;

    @Test
    public void populateForms() throws SQLException {
        List<OpenMRSForm> forms = implementationConfigurationRepository.getForms();
        openMRSRepository.populateForms(forms);
        assertNotEquals(0, forms.get(0).getOpenMRSTerminologies().size());
    }
}
