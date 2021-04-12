package org.bahmni_avni_integration.integration_data;

import org.bahmni_avni_integration.integration.data.repository.AbstractSpringTest;
import org.bahmni_avni_integration.integration_data.repository.ErrorRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest(classes = ConnectionFactory.class)
public class ConnectionFactoryExternalTest extends AbstractSpringTest {
    @Autowired
    private ConnectionFactory connectionFactory;

    @Test
    public void connectToOpenMRSDb() throws SQLException {
        Connection mySQLConnection = connectionFactory.getOpenMRSDbConnection();
        mySQLConnection.close();
    }

    @Test
    public void connectToAvniDb() throws SQLException {
        Connection avniConnection = connectionFactory.getAvniConnection();
        avniConnection.close();
    }
}