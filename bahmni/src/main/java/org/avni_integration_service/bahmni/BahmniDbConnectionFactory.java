package org.avni_integration_service.bahmni;

import org.avni_integration_service.util.TxConfigurableConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class BahmniDbConnectionFactory {
    @Autowired
    private BahmniConfig bahmniConfig;

    public Connection getOpenMRSDbConnection() {
        try {
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://localhost:" + bahmniConfig.getOpenMrsMySqlPort() + "/";

            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url + bahmniConfig.getOpenMrsMySqlDatabase(), bahmniConfig.getOpenMrsMySqlUser(), bahmniConfig.getOpenMrsMySqlPassword());
            return new TxConfigurableConnection(connection, bahmniConfig.isTxRollback());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
