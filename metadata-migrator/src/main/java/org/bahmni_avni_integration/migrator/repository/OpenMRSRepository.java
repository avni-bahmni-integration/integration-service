package org.bahmni_avni_integration.migrator.repository;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.domain.ObsDataType;
import org.bahmni_avni_integration.migrator.ConnectionFactory;
import org.bahmni_avni_integration.migrator.domain.*;
import org.bahmni_avni_integration.migrator.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public void populateForms(List<OpenMRSForm> formList) throws SQLException {
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            PreparedStatement formUuidPS = connection.prepareStatement("select uuid from concept where concept_id = ?");
            PreparedStatement formConceptPS = connection.prepareStatement(fileUtil.readFile("/form-elements.sql"));
            for (OpenMRSForm form : formList) {
                addConcept(formConceptPS, form);
                addFormUuid(formUuidPS, form);
            }
        }
    }

    public OpenMRSPersonAttributes getPersonAttributes() throws SQLException {
        OpenMRSPersonAttributes attributes = new OpenMRSPersonAttributes();
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select COALESCE(pat.description, pat.name), pat.uuid  from person_attribute_type pat where pat.format!='org.openmrs.Concept' and pat.retired=false and pat.name not in ('familyNameLocal', 'middleNameLocal', 'primaryContact')");
            while (resultSet.next()) {
                attributes.add(OpenMRSPersonAttribute.createPrimitive(resultSet.getString(2), resultSet.getString(1)));
            }

            PreparedStatement codedAnswerPS = connection.prepareStatement("""
                    select answer.uuid, answer_name.name, cd.name
                    from concept_answer mapping
                             join concept question on question.concept_id = mapping.concept_id
                             join concept answer on answer.concept_id = mapping.answer_concept
                             join concept_name answer_name on answer_name.concept_id = answer.concept_id
                             join concept_name question_name on question_name.concept_id = question.concept_id
                             join person_attribute_type pat on pat.foreign_key=question.concept_id
                             join concept_datatype cd on answer.datatype_id = cd.concept_datatype_id
                    where pat.format='org.openmrs.Concept'
                      and pat.retired=false
                      and answer_name.concept_name_type = 'FULLY_SPECIFIED'
                      and question_name.concept_name_type = 'FULLY_SPECIFIED'
                      and COALESCE(pat.description, pat.name) = ?""");
            resultSet = statement.executeQuery("""
                    select COALESCE(pat.description, pat.name), pat.uuid
                    from person_attribute_type pat
                             join concept question on pat.foreign_key=question.concept_id
                             join concept_name question_name on question_name.concept_id = question.concept_id
                    where pat.format='org.openmrs.Concept'
                      and pat.retired=false
                      and question_name.concept_name_type = 'FULLY_SPECIFIED'""");
            while (resultSet.next()) {
                OpenMRSPersonAttribute codedAttribute = OpenMRSPersonAttribute.createCoded(resultSet.getString(2), resultSet.getString(1));
                attributes.add(codedAttribute);
                codedAnswerPS.setString(1, codedAttribute.getName());
                ResultSet codedAnswers = codedAnswerPS.executeQuery();
                while (codedAnswers.next()) {
                    codedAttribute.addAnswer(OpenMRSConcept.forPersonConceptAndExtract(codedAnswers.getString(1), codedAnswers.getString(2), codedAnswers.getString(3)));
                }
            }
        }
        return attributes;
    }

    public List<OpenMRSConcept> getConcepts() throws SQLException {
        String sql = """
                select c.uuid, cn.name, cdt.name, cn.concept_name_type
                        from concept c
                               join concept_name cn on cn.concept_id = c.concept_id
                               join concept_datatype cdt on cdt.concept_datatype_id = c.datatype_id
                               join concept_class cc on cc.concept_class_id = c.class_id
                        where c.is_set = false
                          and cdt.name not in ('Rule', 'Document', 'Complex')
                          and cn.concept_name_type = 'FULLY_SPECIFIED'
                          and cc.name not in ('LabTest', 'Concept Attribute', 'Drug', 'Image', 'URL', 'Video')
                          and cn.name not like '%[Avni]'""";
        List<OpenMRSConcept> concepts = new ArrayList<>();
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                OpenMRSConcept openMRSConcept = OpenMRSConcept.forConceptExtract(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
                concepts.add(openMRSConcept);
            }

            List<OpenMRSConcept> codedConcepts = concepts.stream().filter(openMRSConcept -> openMRSConcept.getDataType().equals(ObsDataType.Coded.toString())).collect(Collectors.toList());

            String answerSql = """
                    select ac.uuid, acn.name, cd.name
                    from concept c
                             join concept_answer ca on c.concept_id = ca.concept_id
                             join concept ac on ca.answer_concept = ac.concept_id
                             join concept_name acn on acn.concept_id = ac.concept_id
                             join concept_datatype cd on ac.datatype_id = cd.concept_datatype_id
                    where c.uuid = ? and acn.concept_name_type = 'FULLY_SPECIFIED'""";
            PreparedStatement answerPS = connection.prepareStatement(answerSql);
            for (OpenMRSConcept c : codedConcepts) {
                answerPS.setString(1, c.getUuid());
                ResultSet answers = answerPS.executeQuery();
                while (answers.next()) {
                    c.addAnswer(OpenMRSConcept.forPersonConceptAndExtract(answers.getString(1), answers.getString(2), answers.getString(3)));
                }
            }

            return concepts;
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
        if (!conceptSetExists(connection, conceptId, conceptSetId)) {
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
        if (!conceptAnswerExists(connection, conceptId, answerConceptId)) {
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
            form.addTerm(OpenMRSConcept.forFormExtract(resultSet.getString(1), resultSet.getString(2)));
        }
    }
}