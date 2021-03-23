package org.bahmni_avni_integration.migrator.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.domain.MappingGroup;
import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.migrator.domain.OpenMRSConcept;
import org.bahmni_avni_integration.migrator.domain.OpenMRSForm;
import org.bahmni_avni_integration.migrator.domain.OpenMRSPersonAttribute;
import org.bahmni_avni_integration.migrator.repository.AvniRepository;
import org.bahmni_avni_integration.migrator.repository.ImplementationConfigurationRepository;
import org.bahmni_avni_integration.migrator.repository.OpenMRSRepository;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.migrator.util.ObjectJsonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    private static Logger logger = Logger.getLogger(BahmniToAvniService.class);

    public void migrateForms() throws SQLException {
        List<OpenMRSForm> forms = implementationConfigurationRepository.getForms();
        logger.info(String.format("Found %d forms", forms.size()));
        openMRSRepository.populateForms(forms);
        avniRepository.createForms(forms);
        for (OpenMRSForm form : forms) {
            logger.info(String.format("Creating mapping for form: %s", form.getFormName()));
            mappingMetaDataRepository.saveMapping(form.getMappingGroup(), MappingType.EncounterType, form.getUuid(), form.getFormName(), null);
        }
    }

    public void migratePatientAttributes() throws SQLException {
        List<OpenMRSPersonAttribute> personAttributes = openMRSRepository.getPersonAttributes();
        avniRepository.savePersonAttributes(personAttributes);
        for (OpenMRSPersonAttribute openMRSPersonAttribute : personAttributes) {
            mappingMetaDataRepository.saveMapping(MappingGroup.PatientSubject, MappingType.PersonAttributeConcept, openMRSPersonAttribute.getUuid(), openMRSPersonAttribute.getAvniName());
        }
    }

    public void migrateConcepts() throws SQLException {
        List<OpenMRSConcept> concepts = openMRSRepository.getConcepts();
        List<OpenMRSConcept> workingConcepts = OpenMRSConcept.getFullyQualifiedConceptsWherePresent(concepts);
        avniRepository.saveConcepts(workingConcepts);
        for (OpenMRSConcept openMRSConcept : workingConcepts) {
            mappingMetaDataRepository.saveMapping(MappingGroup.Observation, MappingType.Concept, openMRSConcept.getUuid(), openMRSConcept.getAvniConceptName());
        }
    }
}