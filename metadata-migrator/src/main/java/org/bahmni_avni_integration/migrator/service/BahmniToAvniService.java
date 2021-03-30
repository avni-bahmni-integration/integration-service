package org.bahmni_avni_integration.migrator.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.migrator.config.AvniConfig;
import org.bahmni_avni_integration.migrator.domain.OpenMRSConcept;
import org.bahmni_avni_integration.migrator.domain.OpenMRSForm;
import org.bahmni_avni_integration.migrator.domain.OpenMRSPersonAttribute;
import org.bahmni_avni_integration.migrator.domain.OpenMRSPersonAttributes;
import org.bahmni_avni_integration.migrator.repository.AvniRepository;
import org.bahmni_avni_integration.migrator.repository.ImplementationConfigurationRepository;
import org.bahmni_avni_integration.migrator.repository.OpenMRSRepository;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.migrator.repository.avni.AvniEncounterTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private AvniConfig avniConfig;

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
        logger.info("Bahmni forms created in Avni");

        OpenMRSForm labForm = openMRSRepository.getLabForm();
        avniRepository.createForms(Collections.singletonList(labForm));
        logger.info("Lab form created in Avni");
    }

    public void migratePatientAttributes() throws SQLException {
        OpenMRSPersonAttributes personAttributes = openMRSRepository.getPersonAttributes();
        avniRepository.savePersonAttributes(personAttributes);
        OpenMRSForm bahmniEncounterForm = personAttributes.createForm();
        avniRepository.createForms(Collections.singletonList(bahmniEncounterForm));
        for (OpenMRSPersonAttribute openMRSPersonAttribute : personAttributes) {
            mappingMetaDataRepository.saveMapping(MappingGroup.PatientSubject, MappingType.PersonAttributeConcept, openMRSPersonAttribute.getUuid(), openMRSPersonAttribute.getAvniName());
        }
        logger.info("Patient attributes mapping to Avni completed");
        logger.info("Patient attributes migration completed");
    }

    public void migrateConcepts() throws SQLException {
        List<OpenMRSConcept> concepts = openMRSRepository.getConcepts();
        avniRepository.saveConcepts(concepts);
        for (OpenMRSConcept openMRSConcept : concepts) {
            ObsDataType dataTypeHint = openMRSConcept.getAvniDataType().equals(ObsDataType.Coded.name()) ? ObsDataType.Coded : null;
            mappingMetaDataRepository.saveMapping(MappingGroup.Observation, MappingType.Concept, openMRSConcept.getUuid(), openMRSConcept.getAvniName(), dataTypeHint);
        }
        logger.info("Saved mapping for concepts");
    }

    public void createStandardMetadata() throws SQLException {
        avniRepository.createConcept(ObsDataType.Text, Names.BahmniEntityUuid);

        List<Map<String, String>> standardMappings = implementationConfigurationRepository.getStandardMappings();
        Map<String, String> labMappingType = standardMappings.stream().filter(stringStringMap -> stringStringMap.get("MappingType").equals(MappingType.LabEncounterType.name())).findFirst().orElse(null);
        if (labMappingType != null) {
            avniRepository.createEncounterType(labMappingType.get("Avni Value"), avniConfig.getImplementationUserId());
        }
        logger.info("Standard Metadata created");
    }

    public void cleanup() throws SQLException {
        avniRepository.cleanup();
    }
}