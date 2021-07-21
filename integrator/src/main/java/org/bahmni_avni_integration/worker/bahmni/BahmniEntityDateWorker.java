package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.integration_data.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class BahmniEntityDateWorker {
    @Autowired
    private ConnectionFactory connectionFactory;

    private static final String UPDATE_VISIT_START_DATE_SQL = """
            update ignore visit
                    join visit_attribute va on visit.visit_id = va.visit_id
                    join visit_type on visit.visit_type_id = visit_type.visit_type_id
                    join visit_attribute_type on va.attribute_type_id = visit_attribute_type.visit_attribute_type_id
                set visit.date_started = date(va.value_reference)
                where visit.voided = false and visit_type.name like '%Enrolment [Avni]' and visit_attribute_type.name = 'Event Date [Avni]'
                and abs(datediff(date(va.value_reference), visit.date_started)) > 1
                """;

    private static final String UPDATE_ENCOUNTER_START_DATE_SQL = """
            update encounter
               join encounter_type et on encounter.encounter_type = et.encounter_type_id and et.name like '%[Avni]%'
               join obs on encounter.encounter_id = obs.encounter_id and abs(datediff(encounter.encounter_datetime, obs.value_datetime)) > 1
               join concept_name on obs.concept_id = concept_name.concept_id and concept_name.name = 'Event Date [Avni]' and concept_name_type = 'FULLY_SPECIFIED'
           set encounter.encounter_datetime = obs.value_datetime
           where encounter.voided = false
            """;

    public void fixVisitDates() throws SQLException {
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            connection.createStatement().executeUpdate(UPDATE_VISIT_START_DATE_SQL);
            connection.createStatement().executeUpdate(UPDATE_ENCOUNTER_START_DATE_SQL);
        }
    }
}
