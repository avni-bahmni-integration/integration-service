package org.bahmni_avni_integration.migrator.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.migrator.ConnectionFactory;
import org.bahmni_avni_integration.migrator.domain.AvniConcept;
import org.bahmni_avni_integration.migrator.domain.AvniForm;
import org.bahmni_avni_integration.migrator.domain.AvniFormElementGroup;
import org.bahmni_avni_integration.migrator.repository.AvniRepository;
import org.bahmni_avni_integration.migrator.repository.ImplementationConfigurationRepository;
import org.bahmni_avni_integration.migrator.repository.OpenMRSRepository;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static java.util.Map.entry;

@Service
public class AvniToBahmniService {
    private final OpenMRSRepository openMRSRepository;
    private final AvniRepository avniRepository;
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final ConnectionFactory connectionFactory;
    private final ImplementationConfigurationRepository implementationConfigurationRepository;
    private static Logger logger = Logger.getLogger(AvniToBahmniService.class);

    public AvniToBahmniService(OpenMRSRepository openMRSRepository,
                               AvniRepository avniRepository,
                               MappingMetaDataRepository mappingMetaDataRepository,
                               ConnectionFactory connectionFactory, ImplementationConfigurationRepository implementationConfigurationRepository) {
        this.openMRSRepository = openMRSRepository;
        this.avniRepository = avniRepository;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.connectionFactory = connectionFactory;
        this.implementationConfigurationRepository = implementationConfigurationRepository;
    }

    public void migrateForms() throws SQLException {
        var forms = avniRepository.getForms();
        try (var connection = connectionFactory.getOpenMRSDbConnection()) {
            createForms(forms, connection);
        } catch (SQLException sqlException) {
            logger.error("Could not migrate forms", sqlException);
            throw sqlException;
        }
    }

    private void createForms(List<AvniForm> forms, Connection connection) throws SQLException {
        for (var form : forms) {
            String bahmniFormConceptUuid = UUID.randomUUID().toString();
            var conceptResult = openMRSRepository.createConceptSet(connection, bahmniFormConceptUuid, form.getName());
            int formConceptId = conceptResult.conceptId();
            logger.debug("Form: %s Concept Id: %d".formatted(form.getName(), formConceptId));
            saveFormMapping(form, bahmniFormConceptUuid);
            var formElementGroups = form.getFormElementGroups();
            for (AvniFormElementGroup formElementGroup : formElementGroups) {
                createQuestions(connection, formElementGroup, formConceptId);
            }
        }
    }

    private void createQuestions(Connection connection, AvniFormElementGroup formElementGroup, int formConceptId) throws SQLException {
        var formElements = formElementGroup.getAvniFormElements();
        for (int i = 0; i < formElements.size(); i++) {
            var formElement = formElements.get(i);
            var concept = formElement.getConcept();
            var bahmniQuestionConceptUuid = UUID.randomUUID().toString();
            var conceptResult = openMRSRepository.createConcept(connection,
                    bahmniQuestionConceptUuid, concept.getName(), concept.getDataType(), "Misc", false);
            var questionConceptId = conceptResult.conceptId();
            openMRSRepository.addToConceptSet(connection, questionConceptId, formConceptId, i);
            saveObsMapping(concept.getName(), bahmniQuestionConceptUuid, ObsDataType.parseAvniDataType(concept.getDataType()));

            if (Objects.equals("Coded", formElement.getConcept().getDataType())) {
                createAnswers(connection, concept, questionConceptId);
            }
        }
    }

    private void saveObsMapping(String avniValue, String bahmniValue) {
        saveObsMapping(avniValue, bahmniValue, null);
    }

    private void saveObsMapping(String avniValue, String bahmniValue, ObsDataType obsDataType) {
        var existingMapping = mappingMetaDataRepository.findByMappingGroupAndMappingTypeAndAvniValue(MappingGroup.Observation,
                MappingType.Concept, avniValue);
        if (existingMapping == null) {
            mappingMetaDataRepository.saveMapping(MappingGroup.Observation,
                    MappingType.Concept,
                    bahmniValue,
                    avniValue,
                    obsDataType
            );
        }
    }

    private void saveFormMapping(AvniForm avniForm, String bahmniValue) {
        mappingMetaDataRepository.saveMapping(avniForm.getMappingGroup(), avniForm.getMappingType(), bahmniValue, avniForm.getAvniValueForMapping());
    }

    private void createAnswers(Connection connection, AvniConcept concept, int questionConceptId) throws SQLException {
        var answerConcepts = concept.getAnswerConcepts();
        for (int i = 0; i < answerConcepts.size(); i++) {
            var answerConcept = answerConcepts.get(i);
            var bahmniAnswerConceptUuid = UUID.randomUUID().toString();
            var conceptResult = openMRSRepository.createConcept(connection, bahmniAnswerConceptUuid, answerConcept.getName(), "N/A", "Misc", false);
            int answerConceptId = conceptResult.conceptId();
            openMRSRepository.createConceptAnswer(connection, questionConceptId, answerConceptId, i);
            saveObsMapping(answerConcept.getName(), bahmniAnswerConceptUuid);
        }
    }

    public void cleanup() throws SQLException {
        openMRSRepository.cleanup();
    }

    public void createStandardMetadata() throws SQLException {
        Map<String, Object> constants = implementationConfigurationRepository.getConstants();

        try (var connection = connectionFactory.getOpenMRSDbConnection()) {
            var entityConceptUuid = UUID.randomUUID().toString();
            String entityConceptName = "Avni Entity UUID";
            createStandardConceptAndMapping(connection,
                    entityConceptUuid,
                    entityConceptName,
                    "Text",
                    null,
                    "External uuid is used to match entities after first save");

            var standardConceptMappings = standardConceptMappings();
            for (Map<String, String> mapping : standardConceptMappings) {
                var conceptUuid = UUID.randomUUID().toString();
                String conceptName = mapping.get("conceptName");
                String conceptDataType = mapping.get("dataType");
                createStandardConceptAndMapping(connection, conceptUuid, conceptName, conceptDataType, conceptName, null);
            }

            openMRSRepository.createLocation(connection, "Community", (String) constants.get(ConstantKey.IntegrationBahmniLocation.name()));
            openMRSRepository.createVisitType(connection, "Community", (String) constants.get(ConstantKey.IntegrationBahmniVisitType.name()));
        }
    }

    private void createStandardConceptAndMapping(Connection connection, String conceptUuid, String conceptName, String conceptDataType, String avniValue, String about) throws SQLException {
        openMRSRepository.createConcept(connection, conceptUuid, conceptName, conceptDataType, "Misc", false);
        mappingMetaDataRepository.save(mappingMetadata(MappingGroup.Observation,
                MappingType.Concept,
                conceptUuid,
                avniValue,
                about,
                ObsDataType.parseAvniDataType(conceptDataType)));
    }

    private List<Map<String, String>> standardConceptMappings() {
        List<Map<String, String>> mappings = new ArrayList<>();
        mappings.add(Map.ofEntries(
                entry("conceptName", "Registration date"),
                entry("dataType", "Text")));

        mappings.add(Map.ofEntries(
                entry("conceptName", "First name"),
                entry("dataType", "Text")));

        mappings.add(Map.ofEntries(
                entry("conceptName", "Last name"),
                entry("dataType", "Text")));

        mappings.add(Map.ofEntries(
                entry("conceptName", "Date of birth"),
                entry("dataType", "Date")));

        mappings.add(Map.ofEntries(
                entry("conceptName", "Gender"),
                entry("dataType", "Text")));
        return mappings;
    }

    public void migratePrograms() throws SQLException {
        List<String> programs = avniRepository.getPrograms();
        try (var connection = connectionFactory.getOpenMRSDbConnection()) {
            for (String avniProgramName : programs) {
                var openMrsEncounterTypeUuid = UUID.randomUUID().toString();
                openMRSRepository.createEncounterType(connection, String.format("%s Community Enrolment", avniProgramName), openMrsEncounterTypeUuid);
                mappingMetaDataRepository.save(mappingMetadata(MappingGroup.ProgramEnrolment,
                        MappingType.CommunityEnrolment_EncounterType,
                        openMrsEncounterTypeUuid,
                        avniProgramName,
                        "Encounter type in OpenMRS for community enrolment data in Avni",
                        null));
            }
        } catch (SQLException sqlException) {
            logger.error("Could not migrate programs", sqlException);
            throw sqlException;
        }
    }

    private MappingMetaData mappingMetadata(MappingGroup mappingGroup, MappingType mappingType, String bahmniValue, String avniValue, String about, ObsDataType obsDataType) {
        MappingMetaData mappingMetaData = new MappingMetaData();
        mappingMetaData.setMappingGroup(mappingGroup);
        mappingMetaData.setMappingType(mappingType);
        mappingMetaData.setBahmniValue(bahmniValue);
        mappingMetaData.setAvniValue(avniValue);
        mappingMetaData.setAbout(about);
        mappingMetaData.setDataTypeHint(obsDataType);
        return mappingMetaData;
    }
}