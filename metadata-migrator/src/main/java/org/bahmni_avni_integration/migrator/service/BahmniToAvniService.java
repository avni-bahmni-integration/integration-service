package org.bahmni_avni_integration.migrator.service;

import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.migrator.domain.OpenMRSForm;
import org.bahmni_avni_integration.migrator.repository.AvniRepository;
import org.bahmni_avni_integration.migrator.repository.ImplementationConfigurationRepository;
import org.bahmni_avni_integration.migrator.repository.OpenMRSRepository;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class BahmniToAvniService {
    @Autowired
    private OpenMRSRepository openMRSRepository;

    @Autowired
    private ImplementationConfigurationRepository implementationConfigurationRepository;

    @Autowired
    private AvniRepository avniRepository;

    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    public void migrateForms() throws SQLException {
        List<OpenMRSForm> forms = implementationConfigurationRepository.getForms("bahmni-forms.json");
        openMRSRepository.populateForms(forms);
        avniRepository.createForms(forms);
        for (OpenMRSForm form : forms) {
            mappingMetaDataRepository.saveMapping(form.getMappingGroup(), MappingType.EncounterType, form.getUuid(), form.getFormName(), null);
        }
    }
}