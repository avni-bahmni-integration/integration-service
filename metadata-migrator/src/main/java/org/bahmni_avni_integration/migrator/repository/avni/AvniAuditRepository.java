package org.bahmni_avni_integration.migrator.repository.avni;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AvniAuditRepository {
    private static final String auditInsert = """
            insert into audit (uuid, created_by_id, last_modified_by_id, created_date_time, last_modified_date_time) values (uuid_generate_v4(), ?, ?, now(), now())""";
    private static final String lastAuditId = "select max(id) from audit where created_by_id = ?";

    private static Logger logger = Logger.getLogger(AvniAuditRepository.class);
    private Connection connection;

    public AvniAuditRepository(Connection connection) {
        this.connection = connection;
    }

    public int createAudit(int auditUserId) throws SQLException {
        PreparedStatement psAuditInsert = connection.prepareStatement(auditInsert);
        psAuditInsert.setInt(1, auditUserId);
        psAuditInsert.setInt(2, auditUserId);
        psAuditInsert.executeUpdate();

        PreparedStatement lastAuditId = connection.prepareStatement(AvniAuditRepository.lastAuditId);
        lastAuditId.setInt(1, auditUserId);
        ResultSet resultSet = lastAuditId.executeQuery();
        resultSet.next();
        return resultSet.getInt(1);
    }
}