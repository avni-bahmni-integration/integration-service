package org.bahmni_avni_integration.migrator.service;

import org.bahmni_avni_integration.migrator.domain.OpenMRSForm;
import org.bahmni_avni_integration.migrator.repository.AvniRepository;
import org.bahmni_avni_integration.migrator.repository.ImplementationConfigurationRepository;
import org.bahmni_avni_integration.migrator.repository.OpenMRSRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
public class BahmniToAvniService {
    @Autowired
    private OpenMRSRepository openMRSRepository;
    @Autowired
    private ImplementationConfigurationRepository implementationConfigurationRepository;
    @Autowired
    private AvniRepository avniRepository;

    public void migrateForms() throws SQLException {
        List<OpenMRSForm> forms = implementationConfigurationRepository.getForms("bahmni-forms.json");
        openMRSRepository.addConceptsToForms(forms);
        avniRepository.createForms(forms);
    }
}