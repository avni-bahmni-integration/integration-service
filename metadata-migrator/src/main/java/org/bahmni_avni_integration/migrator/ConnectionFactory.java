package org.bahmni_avni_integration.migrator;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.bahmni_avni_integration.migrator.config.AvniConfig;
import org.bahmni_avni_integration.migrator.config.BahmniConfig;
import org.bahmni_avni_integration.migrator.util.TxConfigurableConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

@Component
public class ConnectionFactory {
    @Autowired
    private BahmniConfig bahmniConfig;

    @Autowired
    private AvniConfig avniConfig;

    @Value("${app.config.tx.rollback}")
    private boolean txRollback;

    public Connection getOpenMRSDbConnection() {
        try {
            JSch jsch = new JSch();
            jsch.addIdentity(bahmniConfig.getSshPrivateKey());
            Session session = jsch.getSession(bahmniConfig.getSshUser(), bahmniConfig.getSshHost(), bahmniConfig.getSshPort());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            session.setPortForwardingL(bahmniConfig.getLocalPort(), bahmniConfig.getOpenMrsMySqlServerFromSSHHost(), bahmniConfig.getOpenMrsMySqlPort());

            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://" + bahmniConfig.getOpenMrsMySqlServerFromSSHHost() + ":" + bahmniConfig.getLocalPort() + "/";

            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url + bahmniConfig.getOpenMrsMySqlDatabase(), bahmniConfig.getOpenMrsMySqlUser(), bahmniConfig.getOpenMrsMySqlPassword());
            return new TxConfigurableConnection(connection, txRollback);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getAvniConnection() {
        try {
            String driver = "org.postgresql.Driver";
            String url = "jdbc:postgresql://localhost:" + avniConfig.getLocalPort() + "/";

            Class.forName(driver);
            Connection connection = DriverManager.getConnection(url + avniConfig.getAvniPostgresDatabase(), avniConfig.getAvniPostgresUser(), avniConfig.getAvniPostgresPassword());
            Statement statement = connection.createStatement();
            statement.execute("set role bahmni_ashwini_integration");
            statement.close();
            return new TxConfigurableConnection(connection, txRollback);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}