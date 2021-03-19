package org.bahmni_avni_integration.migrator.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.domain.MappingGroup;
import org.bahmni_avni_integration.integration_data.domain.MappingMetaData;
import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.integration_data.domain.ObsDataType;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.migrator.ConnectionFactory;
import org.bahmni_avni_integration.migrator.domain.AvniConcept;
import org.bahmni_avni_integration.migrator.domain.AvniForm;
import org.bahmni_avni_integration.migrator.domain.AvniFormElement;
import org.bahmni_avni_integration.migrator.domain.CreateConceptResult;
import org.bahmni_avni_integration.migrator.repository.AvniRepository;
import org.bahmni_avni_integration.migrator.repository.OpenMRSRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Connection;
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
        List<AvniForm> forms = avniRepository.getForms();
        try (var connection = connectionFactory.getOpenMRSDbConnection()) {
            createForms(forms, connection);
        } catch (SQLException sqlException) {
            logger.error("Could not migrate forms", sqlException);
        }
    }

    private void createForms(List<AvniForm> forms, Connection connection) throws SQLException {
        for (var form : forms) {
            CreateConceptResult conceptResult = openMRSRepository.createConcept(connection,
                    UUID.randomUUID().toString(), form.getName(), form.getName(), "N/A", "Misc", true);
            int formConceptId = conceptResult.conceptId();
            logger.debug("Form: %s Concept Id: %d".formatted(form.getName(), formConceptId));
            createGroups(connection, form, formConceptId);
        }
    }

    private void createGroups(Connection connection, AvniForm form, int formConceptId) throws SQLException {
        var formElementGroups = form.getFormElementGroups();
        for (int i = 0; i < formElementGroups.size(); i++) {
            var formElementGroup = formElementGroups.get(i);
            CreateConceptResult conceptResult = openMRSRepository.createConcept(connection,
                    UUID.randomUUID().toString(), formElementGroup.getName(), formElementGroup.getName(), "N/A", "Misc", true);
            int groupConceptId = conceptResult.conceptId();
            openMRSRepository.createConceptSet(connection, groupConceptId, formConceptId, i);
            createQuestions(connection, formElementGroup, groupConceptId);
        }
    }

    private void createQuestions(Connection connection, org.bahmni_avni_integration.migrator.domain.AvniFormElementGroup formElementGroup, int groupConceptId) throws SQLException {
        List<AvniFormElement> formElements = formElementGroup.getAvniFormElements();
        for (int i = 0; i < formElements.size(); i++) {
            AvniFormElement formElement = formElements.get(i);
            AvniConcept concept = formElement.getConcept();
            String bahmniQuestionConceptUuid = UUID.randomUUID().toString();
            CreateConceptResult conceptResult = openMRSRepository.createConcept(connection,
                    bahmniQuestionConceptUuid, concept.getName(), concept.getName(), concept.getDataType(), "Misc", false);
            int questionConceptId = conceptResult.conceptId();
            openMRSRepository.createConceptSet(connection, questionConceptId, groupConceptId, i);
            saveMapping(concept, bahmniQuestionConceptUuid, ObsDataType.parseAvniDataType(concept.getDataType()));

            if (Objects.equals("Coded", formElement.getConcept().getDataType())) {
                createAnswers(connection, concept, questionConceptId);
            }
        }
    }

    private void saveMapping(AvniConcept concept, String bahmniQuestionConceptUuid, ObsDataType obsDataType) {
        String avniConceptName = concept.getName();
        MappingMetaData existingMapping = mappingMetaDataRepository.findByMappingGroupAndMappingTypeAndAvniValue(MappingGroup.Observation,
                MappingType.Concept, avniConceptName);
        if(existingMapping == null) {
            mappingMetaDataRepository.saveMapping(MappingGroup.Observation,
                    MappingType.Concept,
                    bahmniQuestionConceptUuid,
                    concept.getName(),
                    obsDataType
            );
        }
    }

    private void createAnswers(Connection connection, AvniConcept concept, int questionConceptId) throws SQLException {
        List<AvniConcept> answerConcepts = concept.getAnswerConcepts();
        for (int i = 0; i < answerConcepts.size(); i++) {
            AvniConcept answerConcept = answerConcepts.get(i);
            String bahmniAnswerConceptUuid = UUID.randomUUID().toString();
            CreateConceptResult conceptResult = openMRSRepository.createConcept(connection, bahmniAnswerConceptUuid, answerConcept.getName(), answerConcept.getName(), "N/A", "Misc", false);
            int answerConceptId = conceptResult.conceptId();
            openMRSRepository.createConceptAnswer(connection, questionConceptId, answerConceptId, i);
            saveMapping(answerConcept, bahmniAnswerConceptUuid, null);
        }
    }
}