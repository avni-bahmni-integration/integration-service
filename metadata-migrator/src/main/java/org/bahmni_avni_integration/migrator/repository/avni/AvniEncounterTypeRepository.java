package org.bahmni_avni_integration.migrator.repository.avni;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.migrator.repository.AvniRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AvniEncounterTypeRepository {
    private static final String operationalEncounterTypeInsert = "insert into operational_encounter_type (uuid, organisation_id, encounter_type_id, version, name, audit_id) values (uuid_generate_v4(), (select id from organisation), (select id from encounter_type where name = ?), 0, ?, create_audit(?))";
    private static final String encounterTypeInsert = "insert into encounter_type (name, uuid, version, audit_id, organisation_id) values (?, uuid_generate_v4(), 0, create_audit(?), (select id from organisation))";
    private final PreparedStatement operationalEncounterTypePS;
    private final PreparedStatement encounterTypePS;

    private static final Logger logger = Logger.getLogger(AvniEncounterTypeRepository.class);

    public AvniEncounterTypeRepository(Connection connection) throws SQLException {
        operationalEncounterTypePS = connection.prepareStatement(operationalEncounterTypeInsert);
        encounterTypePS = connection.prepareStatement(encounterTypeInsert);
    }

    public void create(String name, int auditUser) throws SQLException {
        encounterTypePS.setString(1, name);
        encounterTypePS.setInt(2, auditUser);
        encounterTypePS.executeUpdate();
        logger.info("Created encounter type: " + name);

        operationalEncounterTypePS.setString(1, name);
        operationalEncounterTypePS.setString(2, name);
        operationalEncounterTypePS.setInt(3, auditUser);
        operationalEncounterTypePS.executeUpdate();
        logger.info("Created operational encounter type: " + name);
    }
}