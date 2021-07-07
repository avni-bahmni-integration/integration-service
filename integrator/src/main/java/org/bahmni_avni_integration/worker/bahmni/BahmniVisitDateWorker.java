package org.bahmni_avni_integration.worker.bahmni;

import org.bahmni_avni_integration.integration_data.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class BahmniVisitDateWorker {
    @Autowired
    private ConnectionFactory connectionFactory;

    private static final String UPDATE_VISIT_START_DATE_SQL = """
        update visit
                join visit_attribute va on visit.visit_id = va.visit_id
                join visit_attribute_type on va.attribute_type_id = visit_attribute_type.visit_attribute_type_id and visit_attribute_type.name = 'Event Date [Avni]' and
                abs(datediff(va.value_reference, visit.date_started)) > 1
            set visit.date_started = date(va.value_reference)
            """;

    public void fixVisitDates() throws SQLException {
        try (Connection connection = connectionFactory.getOpenMRSDbConnection()) {
            connection.createStatement().executeUpdate(UPDATE_VISIT_START_DATE_SQL);
        }
    }
}
