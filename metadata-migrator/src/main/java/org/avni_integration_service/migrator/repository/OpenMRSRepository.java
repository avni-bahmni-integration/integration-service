package org.avni_integration_service.migrator.repository;

import org.apache.log4j.Logger;
import org.avni_integration_service.bahmni.BahmniDbConnectionFactory;
import org.avni_integration_service.util.ObsDataType;
import org.avni_integration_service.migrator.domain.*;
import org.avni_integration_service.integration_data.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class OpenMRSRepository {
    private BahmniDbConnectionFactory connectionFactory;

    private final FileUtil fileUtil;
    private static final Logger logger = Logger.getLogger(OpenMRSRepository.class);

    @Value("${openmrs.txdata.admin.id}")
    private int txDataAdminId;

    @Value("${openmrs.refdata.admin.id}")
    private int refDataAdminId;

    public OpenMRSRepository(BahmniDbConnectionFactory connectionFactory, FileUtil fileUtil) {
        this.connectionFactory = connectionFactory;
        this.fileUtil = fileUtil;
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

    //    todo what happens to observations for concepts of type N/A being direct observation
    public OpenMRSForm getLabForm(String formName) throws SQLException {
        String sql = """
                select distinct cn.name from concept
                join concept_name cn on concept.concept_id = cn.concept_id
                join concept_class cc on concept.class_id = cc.concept_class_id
                join concept_datatype cd on concept.datatype_id = cd.concept_datatype_id
                where cc.name = 'LabTest' and concept.is_set = false and cd.name != 'N/A' and cn.concept_name_type = 'FULLY_SPECIFIED' and cn.name not like '%[Avni]'
                """;
        OpenMRSForm openMRSForm = new OpenMRSForm();
        openMRSForm.setFormName(formName);
        openMRSForm.setType("Encounter");
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                openMRSForm.addTerm(new OpenMRSConceptName(resultSet.getString(1)));
            }
        }
        return openMRSForm;
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
                          and cc.name not in ('Concept Attribute', 'Drug', 'Image', 'URL', 'Video')
                          and cn.name not like '%[Avni]'""";
        List<OpenMRSConcept> concepts = new ArrayList<>();
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                OpenMRSConcept openMRSConcept = OpenMRSConcept.forConceptExtract(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4));
                concepts.add(openMRSConcept);
            }

            List<OpenMRSConcept> codedConcepts = concepts.stream().filter(openMRSConcept -> openMRSConcept.getAvniDataType().equals(ObsDataType.Coded.toString())).collect(Collectors.toList());

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

    public CreateConceptResult createConcept(Connection connection,
                                             String conceptUuid,
                                             String conceptName,
                                             String dataTypeName,
                                             String className,
                                             boolean isSet) throws SQLException {
        String conceptNameWithAvniSuffix = NameMapping.fromAvniNameToBahmni(conceptName);
        int existingConceptId = getConceptIdByFullySpecifiedName(connection, conceptNameWithAvniSuffix);
        if (existingConceptId != -1)
            return new CreateConceptResult(existingConceptId, true);

        var insertConceptPS = connection.prepareStatement("select add_concept_abi_func(?, ?, ?, ?, ?, ?, ?)");
        insertConceptPS.setString(1, conceptNameWithAvniSuffix);
        insertConceptPS.setString(2, conceptName);
        insertConceptPS.setString(3, dataTypeName);
        insertConceptPS.setString(4, className);
        insertConceptPS.setBoolean(5, isSet);
        insertConceptPS.setString(6, conceptUuid);
        insertConceptPS.setInt(7, refDataAdminId);
        ResultSet resultSet = insertConceptPS.executeQuery();
        resultSet.next();
        return new CreateConceptResult(resultSet.getInt(1), false);
    }

    public CreateConceptResult createSetConcept(Connection connection,
                                                String conceptUuid,
                                                String conceptName) throws SQLException {
        String dataTypeName = "N/A";
        String className = "Misc";
        boolean isSet = true;
        return createConcept(connection, conceptUuid, conceptName, dataTypeName, className, isSet);
    }

    public void addToConceptSet(Connection connection, int conceptId, int conceptSetId, double sortWeight) throws SQLException {
        if (!conceptSetExists(connection, conceptId, conceptSetId)) {
            var insertConceptSetPS = connection.prepareStatement("insert into concept_set(concept_id, concept_set, sort_weight, creator, date_created, uuid) values (?, ?, ?, ?, now(), ?)");
            insertConceptSetPS.setInt(1, conceptId);
            insertConceptSetPS.setInt(2, conceptSetId);
            insertConceptSetPS.setDouble(3, sortWeight);
            insertConceptSetPS.setInt(4, refDataAdminId);
            insertConceptSetPS.setString(5, UUID.randomUUID().toString());
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
            var insertConceptAnswerPS = connection.prepareStatement("insert into concept_answer(concept_id, answer_concept, sort_weight, creator, date_created, uuid) values (?, ?, ?, ?, now(), ?)");
            insertConceptAnswerPS.setInt(1, conceptId);
            insertConceptAnswerPS.setInt(2, answerConceptId);
            insertConceptAnswerPS.setDouble(3, sortWeight);
            insertConceptAnswerPS.setInt(4, refDataAdminId);
            insertConceptAnswerPS.setString(5, UUID.randomUUID().toString());
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

    public int getConceptIdByFullySpecifiedName(Connection connection, String conceptFullName) throws SQLException {
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
        if (form.getOpenMRSTerminologies().size() == 0) {
            throw new RuntimeException(String.format("No terminologies found for the form: %d", form.getFormId()));
        }
    }

    public void cleanup() throws SQLException {
        cleanupFunctions();
        cleanupRefData();
    }

    private void cleanupFunctions() throws SQLException {
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            var ps = connection.prepareStatement("DROP FUNCTION IF EXISTS add_concept_abi_func");
            ps.executeUpdate();
        }
    }

    public void cleanupRefData() throws SQLException {
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            deleteRefData("delete from concept_numeric where concept_id in (select concept_id from concept where creator = ?)", connection, "concept_numeric");
            deleteRefData("delete from concept_name where creator = ?", connection, "concept_name");
            deleteRefData("delete from concept_answer where creator = ?", connection, "concept_answer");
            deleteRefData("delete from concept_set where creator = ?", connection, "concept_set");
            deleteRefData("delete from concept where creator = ?", connection, "concept");
            deleteRefData("delete from encounter_type where creator = ?", connection, "encounter_type");
            deleteRefData("delete from location where creator = ?", connection, "location");
            deleteRefData("delete from visit_type where creator = ?", connection, "visit_type");
        }
    }

    public void cleanupTxData() throws SQLException {
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            deleteTxData("delete from obs where creator = ? and previous_version is not null", connection, "Obs");
            deleteTxData("""
                delete
                from obs
                where obs_group_id in (select *
                                       from (select obs_id
                                             from obs
                                             where obs_group_id in
                                                   (select *
                                                    from (select obs_id
                                                          from obs
                                                          where creator = ?
                                                            and obs_group_id is not null) as temp)) as temp2)
                """, connection, "Obs");
            deleteTxData("""
                delete
                from obs
                where obs_group_id in
                      (select * from (select obs_id from obs where creator = ? and obs_group_id is not null) as temp)
                """, connection, "Obs");
            deleteTxData("delete from obs where creator = ? and obs_group_id is not null", connection, "Obs");
            deleteTxData("delete from obs where creator = ?", connection, "Obs");
            deleteTxData("delete from encounter_provider where creator = ?", connection, "encounter_provider");
            deleteTxData("delete from visit_attribute where creator = ?", connection, "visit_attribute");
            deleteTxData("delete from encounter where creator = ?", connection, "encounter");
            deleteTxData("delete from visit where creator = ?", connection, "visit");
            deleteTxData("delete from patient_identifier where creator = ?", connection, "patient_identifier");
            deleteTxData("delete from person_name where creator = ?", connection, "person_name");
            deleteTxData("delete from audit_log where patient_id in (select patient_id from patient where creator = ?)", connection, "audit_log");
            deleteTxData("delete from patient where creator = ?", connection, "patient");
            deleteTxData("delete from person where creator = ?", connection, "person");
        }
    }

    private void deleteTxData(String sql, Connection connection, String entityType) throws SQLException {
        delete(sql, connection, entityType, txDataAdminId);
    }

    private void deleteRefData(String sql, Connection connection, String entityType) throws SQLException {
        delete(sql, connection, entityType, refDataAdminId);
    }

    private void delete(String sql, Connection connection, String entityType, int creatorId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, creatorId);
        int deletedRowCount = preparedStatement.executeUpdate();
        logger.info(String.format("Deleted %d rows of %s", deletedRowCount, entityType));
        preparedStatement.close();
    }


    public void createEncounterType(Connection connection, String name, String uuid) throws SQLException {
        var insertEncounterType = """
                insert into encounter_type (name, date_created, uuid, changed_by, date_changed, creator) values (?, curdate(), ?, ?, curdate(), ?)
                """;
        try (var ps = connection.prepareStatement(insertEncounterType)) {
            ps.setString(1, name);
            ps.setString(2, uuid);
            ps.setInt(3, refDataAdminId);
            ps.setInt(4, refDataAdminId);
            ps.executeUpdate();
        }
    }

    public void createLocation(Connection connection, String name, String uuid) throws SQLException {
        var insertLocation = """
                insert into location (name, uuid, date_created, creator)
                values (?, ?, curdate(), ?)
                """;
        try (var ps = connection.prepareStatement(insertLocation)) {
            ps.setString(1, name);
            ps.setString(2, uuid);
            ps.setInt(3, refDataAdminId);
            ps.executeUpdate();
        }
    }

    public void createVisitType(Connection connection, String name, String uuid) throws SQLException {
        var insertVisitType = """
                insert into visit_type (name, uuid, date_created, creator)
                values (?, ?, curdate(), ?)
                """;
        try (var ps = connection.prepareStatement(insertVisitType)) {
            ps.setString(1, name);
            ps.setString(2, uuid);
            ps.setInt(3, refDataAdminId);
            ps.executeUpdate();
        }
    }

    public void createVisitTypeAttribute(Connection connection, String name, String uuid, String dataType, int minOccurs, int maxOccurs) throws SQLException {
        var insertVisitType = """
                insert into visit_attribute_type (name, uuid, datatype, min_occurs, max_occurs, creator, date_created)
                values (?, ?, ?, ?, ?, ?, curdate())
                """;
        try (var ps = connection.prepareStatement(insertVisitType)) {
            ps.setString(1, name);
            ps.setString(2, uuid);
            ps.setString(3, dataType);
            ps.setInt(4, minOccurs);
            ps.setInt(5, maxOccurs);
            ps.setInt(6, refDataAdminId);
            ps.executeUpdate();
        }
    }

    public void createAddConceptProcedure(Connection connection) throws SQLException {
        try (var ps = connection.prepareStatement(fileUtil.readConfigFile("create_add_concept_abi_func.sql"))) {
            ps.executeUpdate();
        }
    }
}
