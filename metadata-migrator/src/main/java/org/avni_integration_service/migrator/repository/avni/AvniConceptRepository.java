package org.avni_integration_service.migrator.repository.avni;

import org.apache.log4j.Logger;
import org.avni_integration_service.util.FormatAndParseUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.avni_integration_service.util.FormatAndParseUtil.escapedForSql;

public class AvniConceptRepository {
    private static final String conceptInsert = """
            insert into concept (data_type, name, uuid, version, organisation_id, audit_id) 
            VALUES ('%s', '%s', uuid_generate_v4(), 0, (select id from organisation), create_audit(%d)) on conflict do nothing""";
    private static final String conceptInsertCommonAudit = """
            insert into concept (data_type, name, uuid, version, organisation_id, audit_id) 
            VALUES ('%s', '%s', uuid_generate_v4(), 0, (select id from organisation), %d) on conflict do nothing""";
    private static final String conceptAnswerInsert = """
            insert into concept_answer (concept_id, answer_concept_id, uuid, version, answer_order, organisation_id, audit_id)
            values ((select id from concept where name = '%s'),
                    (select id from concept where name = '%s'), uuid_generate_v4(), 0, %d, (select id from organisation), create_audit(%d))""";
    private static final String conceptAnswerInsertCommonAudit = """
            insert into concept_answer (concept_id, answer_concept_id, uuid, version, answer_order, organisation_id, audit_id)
            values ((select id from concept where name = '%s'),
                    (select id from concept where name = '%s'), uuid_generate_v4(), 0, %d, (select id from organisation), %d)""";
    private final Statement statementConceptInsert;
    private final Statement statementConceptAnswerInsert;

    private static final Logger logger = Logger.getLogger(AvniConceptRepository.class);

    public AvniConceptRepository(Connection connection) throws SQLException {
        statementConceptInsert = connection.createStatement();
        statementConceptAnswerInsert = connection.createStatement();
    }

    public void addConcept(String dataType, String conceptName, int auditUser) throws SQLException {
        try {
            String sql = String.format(conceptInsert, dataType, escapedForSql(conceptName), auditUser);
            statementConceptInsert.executeUpdate(sql);
        } catch (SQLException sqlException) {
            logger.error(String.format("Error when creating concept: %s", conceptName));
            throw sqlException;
        }
    }

    public void addConceptToBatch(String dataType, String conceptName, int auditUser) throws SQLException {
        String sql = String.format(conceptInsert, dataType, escapedForSql(conceptName), auditUser);
        statementConceptInsert.addBatch(sql);
    }

    public void addConceptToBatchWithCommonAudit(String dataType, String conceptName, int auditId) throws SQLException {
        String sql = String.format(conceptInsertCommonAudit, dataType, escapedForSql(conceptName), auditId);
        statementConceptInsert.addBatch(sql);
    }

    public void executeConceptBatch() throws SQLException {
        statementConceptInsert.executeBatch();
    }

    public void addConceptAnswer(String parentName, String childName, int order, int auditUser) throws SQLException {
        try {
            String sql = String.format(conceptAnswerInsert, escapedForSql(parentName), escapedForSql(childName), order, auditUser);
            statementConceptAnswerInsert.executeUpdate(sql);
        } catch (SQLException sqlException) {
            logger.error(String.format("Error when adding answer: %s for concept: %s", childName, parentName));
            throw sqlException;
        }
    }

    public void addConceptAnswerToBatch(String parentName, String childName, int order, int auditUser) throws SQLException {
        String sql = String.format(conceptAnswerInsert, escapedForSql(parentName), escapedForSql(childName), order, auditUser);
        statementConceptAnswerInsert.addBatch(sql);
    }

    public void addConceptAnswerToBatchWithCommonAudit(String parentName, String childName, int order, int auditId) throws SQLException {
        String sql = String.format(conceptAnswerInsertCommonAudit, escapedForSql(parentName), escapedForSql(childName), order, auditId);
        statementConceptAnswerInsert.addBatch(sql);
    }

    public void executeConceptAnswerBatch() throws SQLException {
        statementConceptAnswerInsert.executeBatch();
    }
}
