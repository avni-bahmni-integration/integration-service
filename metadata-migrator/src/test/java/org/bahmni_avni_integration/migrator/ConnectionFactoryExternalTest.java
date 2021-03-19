package org.bahmni_avni_integration.migrator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.SQLException;

@SpringBootTest
class ConnectionFactoryExternalTest {
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