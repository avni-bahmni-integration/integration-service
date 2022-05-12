package org.avni_integration_service.integration_data;

import org.avni_integration_service.integration_data.config.AvniConfig;
import org.avni_integration_service.integration_data.util.TxConfigurableConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Component
public class ConnectionFactory {
    @Autowired
    private AvniConfig avniConfig;

    @Value("${app.config.tx.rollback}")
    private boolean txRollback;

    public Connection getAvniConnection() {
        try {
            if (avniConfig.getImplementationOrgDbUser() == null || avniConfig.getImplementationOrgDbUser().isEmpty() || avniConfig.getImplementationOrgDbUser().equals("dummy"))
                throw new RuntimeException("Please set avni.impl_org.db.user property");

            String driver = "org.postgresql.Driver";
            String url = "jdbc:postgresql://localhost:" + avniConfig.getDbPort() + "/";

            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url + avniConfig.getAvniPostgresDatabase(), avniConfig.getAvniPostgresUser(), avniConfig.getAvniPostgresPassword());
            Statement statement = connection.createStatement();
            statement.execute(String.format("set role %s", avniConfig.getImplementationOrgDbUser()));
            statement.close();
            return new TxConfigurableConnection(connection, txRollback);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
