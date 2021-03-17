package org.bahmni_avni_integration.migrator.repository;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.migrator.ConnectionFactory;
import org.bahmni_avni_integration.migrator.domain.*;
import org.bahmni_avni_integration.migrator.util.FileUtil;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class OpenMRSRepository {
    private final ConnectionFactory connectionFactory;
    private static Logger logger = Logger.getLogger(OpenMRSRepository.class);

    public OpenMRSRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }


    public void populateForms(List<OpenMRSForm> formList) throws SQLException {
        try (Connection connection = connectionFactory.getMySqlConnection()) {
            PreparedStatement formUuidPS = connection.prepareStatement("select uuid from concept where concept_id = ?");
            PreparedStatement formConceptPS = connection.prepareStatement(FileUtil.readFile("form-elements.sql"));
            for (OpenMRSForm form : formList) {
                addConcept(formConceptPS, form);
                addFormUuid(formUuidPS, form);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void createOpenMRSForms(List<AvniForm> forms) {
        try (var connection = connectionFactory.getMySqlConnection()) {
            for (var form : forms) {
                int formConceptId = createConcept(connection, form.getName(), form.getName(), "N/A", "Misc", true);
                logger.debug("Form: %s Concept Id: %d".formatted(form.getName(), formConceptId));

                var formElementGroups = form.getFormElementGroups();
                for (int i = 0; i < formElementGroups.size(); i++) {
                    var formElementGroup = formElementGroups.get(i);
                    int groupConceptId = createConcept(connection, formElementGroup.getName(), formElementGroup.getName(), "N/A", "Misc", true);
                    createConceptSet(connection, groupConceptId, formConceptId, i);
                    List<AvniFormElement> formElements = formElementGroup.getAvniFormElements();
                    for (int j = 0; j < formElements.size(); j++) {
                        AvniFormElement formElement = formElements.get(j);
                        AvniConcept concept = formElement.getConcept();
                        int questionConceptId = createConcept(connection, concept.getName(), concept.getName(), concept.getDataType(), "Misc", false);
                        createConceptSet(connection, questionConceptId, groupConceptId, j);

                        if (Objects.equals("Coded", formElement.getConcept().getDataType())) {
                            List<AvniConcept> answerConcepts = concept.getAnswerConcepts();
                            for (int k = 0; k < answerConcepts.size(); k++) {
                                AvniConcept answerConcept = answerConcepts.get(k);
                                int answerConceptId = createConcept(connection, answerConcept.getName(), answerConcept.getName(), "N/A", "Misc", false);
                                createConceptAnswer(connection, questionConceptId, answerConceptId, k);
                            }
                        }
                    }
                }

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private int createConcept(Connection connection, String conceptFullName, String conceptShortName, String dataTypeName, String className, boolean isSet) throws SQLException {
        try {
            String conceptFullNameWithAvniSuffix = String.format("%s [Avni]", conceptFullName);
            int existingConceptId = getConceptId(connection, conceptFullNameWithAvniSuffix);
            if(existingConceptId != -1)
                return existingConceptId;

            var insertConceptPS = connection.prepareStatement("select add_concept_abi_func(?, ?, ?, ?, ?, ?)");
            insertConceptPS.setString(1, conceptFullNameWithAvniSuffix);
            insertConceptPS.setString(2, conceptShortName);
            insertConceptPS.setString(3, dataTypeName);
            insertConceptPS.setString(4, className);
            insertConceptPS.setBoolean(5, isSet);
            insertConceptPS.setString(6, UUID.randomUUID().toString());
            ResultSet resultSet = insertConceptPS.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    private void createConceptSet(Connection connection, int conceptId, int conceptSetId, double sortWeight) throws SQLException {
        try {
            var insertConceptSetPS = connection.prepareStatement("insert into concept_set(concept_id, concept_set, sort_weight, creator, date_created, uuid) values (?, ?, ?, 1, now(), ?)");
            insertConceptSetPS.setInt(1, conceptId);
            insertConceptSetPS.setInt(2, conceptSetId);
            insertConceptSetPS.setDouble(3, sortWeight);
            insertConceptSetPS.setString(4, UUID.randomUUID().toString());
            insertConceptSetPS.executeUpdate();
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    private void createConceptAnswer(Connection connection, int conceptId, int answerConceptId, double sortWeight) throws SQLException {
        try {
            var insertConceptAnswerPS = connection.prepareStatement("insert into concept_answer(concept_id, answer_concept, sort_weight, creator, date_created, uuid) values (?, ?, ?, 1, now(), ?)");
            insertConceptAnswerPS.setInt(1, conceptId);
            insertConceptAnswerPS.setInt(2, answerConceptId);
            insertConceptAnswerPS.setDouble(3, sortWeight);
            insertConceptAnswerPS.setString(4, UUID.randomUUID().toString());
            int i = insertConceptAnswerPS.executeUpdate();
            logger.debug("Insert concept_answer status: %d".formatted(i));
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    private int getConceptId(Connection connection, String conceptFullName) throws SQLException {
        try {
            var getConceptPS = connection.prepareStatement("SELECT concept_id from concept_name where name = BINARY ? and concept_name_type='FULLY_SPECIFIED'");
            getConceptPS.setString(1, conceptFullName);
            var resultSet = getConceptPS.executeQuery();
            boolean conceptExists = resultSet.next();
            if (!conceptExists)
                return -1;

            int concept_id = resultSet.getInt("concept_id");
            if (resultSet.next())
                throw new RuntimeException("Did not expect to find multiple concepts by full name %s".formatted(conceptFullName));
            return concept_id;
        } catch (SQLException sqlException) {
            throw sqlException;
        }
    }

    private void addFormUuid(PreparedStatement formUuidPS, OpenMRSForm form) throws SQLException {
        formUuidPS.setInt(1, form.getFormId());
        ResultSet resultSet = formUuidPS.executeQuery();
        resultSet.next();
        form.setUuid(resultSet.getString(1));
    }

    private void addConcept(PreparedStatement formConceptPS, OpenMRSForm form) throws SQLException {
        formConceptPS.setInt(1, form.getFormId());
        formConceptPS.setInt(2, form.getFormId());
        formConceptPS.setInt(3, form.getFormId());
        formConceptPS.setInt(4, form.getFormId());

        ResultSet resultSet = formConceptPS.executeQuery();
        while (resultSet.next()) {
            form.addConcept(resultSet.getString(1), resultSet.getString(2));
        }
    }
}