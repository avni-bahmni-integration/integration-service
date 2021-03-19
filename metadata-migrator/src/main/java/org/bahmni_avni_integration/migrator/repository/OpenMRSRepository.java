package org.bahmni_avni_integration.migrator.repository;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.migrator.ConnectionFactory;
import org.bahmni_avni_integration.migrator.domain.CreateConceptResult;
import org.bahmni_avni_integration.migrator.domain.OpenMRSForm;
import org.bahmni_avni_integration.migrator.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Component
public class OpenMRSRepository {
    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private FileUtil fileUtil;
    private static Logger logger = Logger.getLogger(OpenMRSRepository.class);

    public OpenMRSRepository(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void populateForms(List<OpenMRSForm> formList) {
        try (Connection connection = connectionFactory.getMySqlConnection()) {
            PreparedStatement formUuidPS = connection.prepareStatement("select uuid from concept where concept_id = ?");
            PreparedStatement formConceptPS = connection.prepareStatement(fileUtil.readFile("/form-elements.sql"));
            for (OpenMRSForm form : formList) {
                addConcept(formConceptPS, form);
                addFormUuid(formUuidPS, form);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public CreateConceptResult createConcept(Connection connection, String conceptUuid, String conceptFullName, String conceptShortName, String dataTypeName, String className, boolean isSet) throws SQLException {
        String conceptFullNameWithAvniSuffix = String.format("%s [Avni]", conceptFullName);
        int existingConceptId = getConceptId(connection, conceptFullNameWithAvniSuffix);
        if (existingConceptId != -1)
            return new CreateConceptResult(existingConceptId, true);

        var insertConceptPS = connection.prepareStatement("select add_concept_abi_func(?, ?, ?, ?, ?, ?)");
        insertConceptPS.setString(1, conceptNameWithAvniSuffix(conceptFullName));
        insertConceptPS.setString(2, conceptShortName);
        insertConceptPS.setString(3, dataTypeName);
        insertConceptPS.setString(4, className);
        insertConceptPS.setBoolean(5, isSet);
        insertConceptPS.setString(6, conceptUuid);
        ResultSet resultSet = insertConceptPS.executeQuery();
        resultSet.next();
        return new CreateConceptResult(resultSet.getInt(1), false);
    }

    public void createConceptSet(Connection connection, int conceptId, int conceptSetId, double sortWeight) throws SQLException {
        if(!conceptSetExists(connection, conceptId, conceptSetId)) {
            var insertConceptSetPS = connection.prepareStatement("insert into concept_set(concept_id, concept_set, sort_weight, creator, date_created, uuid) values (?, ?, ?, 1, now(), ?)");
            insertConceptSetPS.setInt(1, conceptId);
            insertConceptSetPS.setInt(2, conceptSetId);
            insertConceptSetPS.setDouble(3, sortWeight);
            insertConceptSetPS.setString(4, UUID.randomUUID().toString());
            insertConceptSetPS.executeUpdate();
        }
    }

    private boolean conceptSetExists(Connection connection, int conceptId, int conceptSetId) throws SQLException {
        var conceptSetSelectPS = connection.prepareStatement("select * from concept_set where concept_id = ? and concept_set = ?");
        conceptSetSelectPS.setInt(1, conceptId);
        conceptSetSelectPS.setInt(2, conceptSetId);
        ResultSet resultSet = conceptSetSelectPS.executeQuery();
        return resultSet.next();
    }

    public void createConceptAnswer(Connection connection, int conceptId, int answerConceptId, double sortWeight) throws SQLException {
        if(!conceptAnswerExists(connection, conceptId, answerConceptId)) {
            var insertConceptAnswerPS = connection.prepareStatement("insert into concept_answer(concept_id, answer_concept, sort_weight, creator, date_created, uuid) values (?, ?, ?, 1, now(), ?)");
            insertConceptAnswerPS.setInt(1, conceptId);
            insertConceptAnswerPS.setInt(2, answerConceptId);
            insertConceptAnswerPS.setDouble(3, sortWeight);
            insertConceptAnswerPS.setString(4, UUID.randomUUID().toString());
            insertConceptAnswerPS.executeUpdate();
        }
    }

    private boolean conceptAnswerExists(Connection connection, int conceptId, int answerConceptId) throws SQLException {
        var conceptAnswerSelectPS = connection.prepareStatement("select * from concept_answer where concept_id = ? and answer_concept = ?");
        conceptAnswerSelectPS.setInt(1, conceptId);
        conceptAnswerSelectPS.setInt(2, answerConceptId);
        ResultSet resultSet = conceptAnswerSelectPS.executeQuery();
        return resultSet.next();
    }

    private String conceptNameWithAvniSuffix(String conceptName) {
        return String.format("%s [Avni]", conceptName);
    }

    private int getConceptId(Connection connection, String conceptFullName) throws SQLException {
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