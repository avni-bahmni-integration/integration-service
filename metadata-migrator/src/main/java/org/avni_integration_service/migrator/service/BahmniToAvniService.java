package org.avni_integration_service.migrator.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.bahmni.BahmniMappingGroup;
import org.avni_integration_service.bahmni.BahmniMappingType;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.domain.*;
import org.avni_integration_service.integration_data.repository.IgnoredIntegratingConceptRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.migrator.domain.*;
import org.avni_integration_service.migrator.repository.AvniRepository;
import org.avni_integration_service.migrator.repository.BahmniConfigurationRepository;
import org.avni_integration_service.migrator.repository.OpenMRSRepository;
import org.avni_integration_service.util.ObsDataType;
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
    private BahmniConfigurationRepository implementationConfigurationRepository;

    @Autowired
    private AvniRepository avniRepository;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    @Autowired
    private IgnoredIntegratingConceptRepository ignoredBahmniConceptRepository;

    @Autowired
    private BahmniMappingGroup bahmniMappingGroup;

    @Autowired
    private BahmniMappingType bahmniMappingType;
    private static final Logger logger = Logger.getLogger(BahmniToAvniService.class);

    public void migrateForms() throws SQLException {
        List<OpenMRSForm> forms = implementationConfigurationRepository.getForms();
        logger.info(String.format("Found %d forms", forms.size()));
        migrateForms(forms);
    }

    public void migrateForms(List<OpenMRSForm> forms) throws SQLException {
        openMRSRepository.populateForms(forms);
        avniRepository.createForms(forms);
        for (OpenMRSForm form : forms) {
            logger.info(String.format("Creating mapping for form: %s", form.getFormName()));
            mappingService.saveMapping(form.getMappingGroup(bahmniMappingGroup), bahmniMappingType.encounterType, form.getUuid(), form.getFormName(), null);
            if (form.getProgram() != null)
                mappingService.saveMapping(bahmniMappingGroup.programEnrolment, bahmniMappingType.bahmniFormCommunityProgram, form.getUuid(), form.getProgram(), null);
        }
        logger.info("Bahmni forms created in Avni");
    }

    public void migratePatientAttributes() throws SQLException {
        OpenMRSPersonAttributes personAttributes = openMRSRepository.getPersonAttributes();
        avniRepository.savePersonAttributes(personAttributes);
        OpenMRSForm bahmniEncounterForm = personAttributes.createForm();
        avniRepository.createForms(Collections.singletonList(bahmniEncounterForm));
        for (OpenMRSPersonAttribute openMRSPersonAttribute : personAttributes) {
            mappingService.saveMapping(bahmniMappingGroup.patientSubject, bahmniMappingType.personAttributeConcept, openMRSPersonAttribute.getUuid(), openMRSPersonAttribute.getAvniName());
        }
        logger.info("Patient attributes mapping to Avni completed");
        logger.info("Patient attributes migration completed");
    }

    public void migrateConcepts() throws SQLException {
        createAvniConcepts();
        createOrUpdateConceptMapping();
    }

    public void createAvniConcepts() throws SQLException {
        List<OpenMRSConcept> concepts = openMRSRepository.getConcepts();
        avniRepository.saveConcepts(concepts, implementationConfigurationRepository.getConstants());
    }

    public void createOrUpdateConceptMapping() throws SQLException {
        logger.info("Creating concept mapping for concepts present in Bahmni");
        List<OpenMRSConcept> concepts = openMRSRepository.getConcepts();
        logger.info(String.format("Found %d concepts in OpenMRS", concepts.size()));
        for (OpenMRSConcept openMRSConcept : concepts) {
            ObsDataType dataTypeHint = openMRSConcept.getAvniDataType().equals(ObsDataType.Coded.name()) ? ObsDataType.Coded : null;
            if (mappingMetaDataRepository.findByMappingGroupAndMappingTypeAndIntSystemValue(bahmniMappingGroup.observation, bahmniMappingType.concept, openMRSConcept.getUuid()) == null) {
                mappingService.saveMapping(bahmniMappingGroup.observation, bahmniMappingType.concept, openMRSConcept.getUuid(), openMRSConcept.getAvniName(), dataTypeHint);
            }
        }
        logger.info("Created concept mappings.");
        List<String> ignoredConcepts = implementationConfigurationRepository.getIgnoredConcepts();
        logger.info(String.format("Found %d ignored concepts configured", ignoredConcepts.size()));
        for (String ignoredConcept : ignoredConcepts) {
            if (ignoredBahmniConceptRepository.findByConceptId(ignoredConcept) == null)
                ignoredBahmniConceptRepository.save(new IgnoredIntegratingConcept(ignoredConcept));
        }
        logger.info("Created ignored concepts.");
    }

    public void createStandardMetadata() throws SQLException {
        StandardMappings standardMappings = implementationConfigurationRepository.getStandardMappings();

        avniRepository.createConcept(ObsDataType.Text, standardMappings.getAvniValueForMappingType(bahmniMappingType.bahmniUUIDConcept));

        Map<String, String> labMappingType = standardMappings.getLabMappingType(bahmniMappingType.labEncounterType);
        if (labMappingType != null) {
            OpenMRSForm labForm = openMRSRepository.getLabForm(labMappingType.get("Avni Value"));
            avniRepository.createForms(Collections.singletonList(labForm));
            logger.info("Lab form and encounter type created in Avni");
        }

        Map<String, String> drugOrderMappingType = standardMappings.getDrugOrderMappingType(bahmniMappingType.drugOrderEncounterType);
        Map<String, String> drugOrderConceptMapping = standardMappings.getDrugOrderConcept(bahmniMappingType.drugOrderConcept);
        if (drugOrderMappingType != null) {
            avniRepository.createConcept(ObsDataType.Text, drugOrderConceptMapping.get("Avni Value"));

            OpenMRSForm drugOrderForm = new OpenMRSForm();
            drugOrderForm.setFormName(drugOrderMappingType.get("Avni Value"));
            drugOrderForm.setType("Encounter");
            drugOrderForm.addTerm(new UserProvidedConceptName(drugOrderConceptMapping.get("Avni Value")));
            avniRepository.createForms(Collections.singletonList(drugOrderForm));
            logger.info("Drug order form, encounter type, and concept created in Avni");
        }

        logger.info("Standard Metadata created");
    }

    public void cleanup() throws SQLException {
        avniRepository.cleanup();
    }
}
