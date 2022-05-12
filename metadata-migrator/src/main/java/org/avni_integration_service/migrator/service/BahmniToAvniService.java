package org.avni_integration_service.migrator.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.integration_data.domain.*;
import org.avni_integration_service.integration_data.repository.IgnoredBahmniConceptRepository;
import org.avni_integration_service.migrator.domain.*;
import org.avni_integration_service.migrator.repository.AvniRepository;
import org.avni_integration_service.migrator.repository.ImplementationConfigurationRepository;
import org.avni_integration_service.migrator.repository.OpenMRSRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
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
    private ImplementationConfigurationRepository implementationConfigurationRepository;

    @Autowired
    private AvniRepository avniRepository;

    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    @Autowired
    private IgnoredBahmniConceptRepository ignoredBahmniConceptRepository;

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
            mappingMetaDataRepository.saveMapping(form.getMappingGroup(), MappingType.EncounterType, form.getUuid(), form.getFormName(), null);
            if (form.getProgram() != null)
                mappingMetaDataRepository.saveMapping(MappingGroup.ProgramEnrolment, MappingType.BahmniForm_CommunityProgram, form.getUuid(), form.getProgram(), null);
        }
        logger.info("Bahmni forms created in Avni");
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
            if (mappingMetaDataRepository.findByMappingGroupAndMappingTypeAndBahmniValue(MappingGroup.Observation, MappingType.Concept, openMRSConcept.getUuid()) == null) {
                mappingMetaDataRepository.saveMapping(MappingGroup.Observation, MappingType.Concept, openMRSConcept.getUuid(), openMRSConcept.getAvniName(), dataTypeHint);
            }
        }
        logger.info("Created concept mappings.");
        List<String> ignoredConcepts = implementationConfigurationRepository.getIgnoredConcepts();
        logger.info(String.format("Found %d ignored concepts configured", ignoredConcepts.size()));
        for (String ignoredConcept : ignoredConcepts) {
            if (ignoredBahmniConceptRepository.findByConceptUuid(ignoredConcept) == null)
                ignoredBahmniConceptRepository.save(new IgnoredBahmniConcept(ignoredConcept));
        }
        logger.info("Created ignored concepts.");
    }

    public void createStandardMetadata() throws SQLException {
        StandardMappings standardMappings = implementationConfigurationRepository.getStandardMappings();

        avniRepository.createConcept(ObsDataType.Text, standardMappings.getAvniValueForMappingType(MappingType.BahmniUUID_Concept));

        Map<String, String> labMappingType = standardMappings.getLabMappingType();
        if (labMappingType != null) {
            OpenMRSForm labForm = openMRSRepository.getLabForm(labMappingType.get("Avni Value"));
            avniRepository.createForms(Collections.singletonList(labForm));
            logger.info("Lab form and encounter type created in Avni");
        }

        Map<String, String> drugOrderMappingType = standardMappings.getDrugOrderMappingType();
        Map<String, String> drugOrderConceptMapping = standardMappings.getDrugOrderConcept();
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
