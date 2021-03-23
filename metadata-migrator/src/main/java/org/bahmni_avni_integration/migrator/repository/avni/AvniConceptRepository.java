package org.bahmni_avni_integration.migrator.repository.avni;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AvniConceptRepository {
    private static final String conceptInsert = """
                        insert into concept (data_type, name, uuid, version, organisation_id, audit_id) 
                        VALUES (?, ?, uuid_generate_v4(), 0, (select id from organisation), create_audit())""";
    private static final String conceptAnswerInsert = """
                insert into concept_answer (concept_id, answer_concept_id, uuid, version, answer_order, organisation_id, audit_id)
                values ((select id from concept where name = ?),
                        (select id from concept where name = ?), uuid_generate_v4(), 0, ?, (select id from organisation), create_audit())
                """;
    private final PreparedStatement psConceptInsert;
    private final PreparedStatement psConceptAnswerInsert;

    public AvniConceptRepository(Connection connection) throws SQLException {
        psConceptInsert = connection.prepareStatement(conceptInsert);
        psConceptAnswerInsert = connection.prepareStatement(conceptAnswerInsert);
    }

    public void addConcept(String dataType, String conceptName) throws SQLException {
        psConceptInsert.setString(1, dataType);
        psConceptInsert.setString(2, conceptName);
        psConceptInsert.executeUpdate();
    }

    public void addConceptAnswer(String parentName, String childName, int order) throws SQLException {
        psConceptAnswerInsert.setString(1, parentName);
        psConceptAnswerInsert.setString(2, childName);
        psConceptAnswerInsert.setInt(3, order);
        psConceptAnswerInsert.executeUpdate();
    }
}