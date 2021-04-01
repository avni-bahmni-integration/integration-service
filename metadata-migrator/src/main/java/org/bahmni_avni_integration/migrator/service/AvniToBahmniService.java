package org.bahmni_avni_integration.migrator.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.domain.MappingGroup;
import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.integration_data.domain.ObsDataType;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.migrator.ConnectionFactory;
import org.bahmni_avni_integration.migrator.domain.AvniConcept;
import org.bahmni_avni_integration.migrator.domain.AvniForm;
import org.bahmni_avni_integration.migrator.domain.AvniFormElementGroup;
import org.bahmni_avni_integration.migrator.repository.AvniRepository;
import org.bahmni_avni_integration.migrator.repository.OpenMRSRepository;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class AvniToBahmniService {
    private final OpenMRSRepository openMRSRepository;
    private final AvniRepository avniRepository;
    private final MappingMetaDataRepository mappingMetaDataRepository;
    private final ConnectionFactory connectionFactory;
    private static Logger logger = Logger.getLogger(AvniToBahmniService.class);

    public AvniToBahmniService(OpenMRSRepository openMRSRepository, AvniRepository avniRepository, MappingMetaDataRepository mappingMetaDataRepository, ConnectionFactory connectionFactory) {
        this.openMRSRepository = openMRSRepository;
        this.avniRepository = avniRepository;
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.connectionFactory = connectionFactory;
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
            var conceptResult = openMRSRepository.createConcept(connection,
                    bahmniFormConceptUuid, form.getName(), form.getName(), "N/A", "Misc", true);
            int formConceptId = conceptResult.conceptId();
            logger.debug("Form: %s Concept Id: %d".formatted(form.getName(), formConceptId));
            saveObsMapping(form.getName(), bahmniFormConceptUuid);
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
                    bahmniQuestionConceptUuid, concept.getName(), concept.getName(), concept.getDataType(), "Misc", false);
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
        if(existingMapping == null) {
            mappingMetaDataRepository.saveMapping(MappingGroup.Observation,
                    MappingType.Concept,
                    bahmniValue,
                    avniValue,
                    obsDataType
            );
        }
    }

    private void saveFormMapping(MappingType mappingType, String avniValue, String bahmniValue, ObsDataType obsDataType) {
        var existingMapping = mappingMetaDataRepository.findByMappingGroupAndMappingTypeAndAvniValue(
                MappingGroup.Observation, mappingType, avniValue);
        if(existingMapping == null) {
            mappingMetaDataRepository.saveMapping(MappingGroup.Observation,
                    MappingType.Concept,
                    bahmniValue,
                    avniValue,
                    obsDataType
            );
        }
    }

    private void createAnswers(Connection connection, AvniConcept concept, int questionConceptId) throws SQLException {
        var answerConcepts = concept.getAnswerConcepts();
        for (int i = 0; i < answerConcepts.size(); i++) {
            var answerConcept = answerConcepts.get(i);
            var bahmniAnswerConceptUuid = UUID.randomUUID().toString();
            var conceptResult = openMRSRepository.createConcept(connection, bahmniAnswerConceptUuid, answerConcept.getName(), answerConcept.getName(), "N/A", "Misc", false);
            int answerConceptId = conceptResult.conceptId();
            openMRSRepository.createConceptAnswer(connection, questionConceptId, answerConceptId, i);
            saveObsMapping(answerConcept.getName(), bahmniAnswerConceptUuid);
        }
    }
}