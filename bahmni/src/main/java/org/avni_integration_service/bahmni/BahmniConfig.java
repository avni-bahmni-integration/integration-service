package org.avni_integration_service.bahmni;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BahmniConfig {
    @Value("${openmrs.mysql.user}")
    private String openMrsMySqlUser;

    @Value("${openmrs.mysql.password}")
    private String openMrsMySqlPassword;

    @Value("${openmrs.mysql.database}")
    private String openMrsMySqlDatabase;

    @Value("${openmrs.mysql.port}")
    private int openMrsMySqlPort;

    @Value("${app.config.tx.rollback}")
    private boolean txRollback;

    public String getOpenMrsMySqlUser() {
        return openMrsMySqlUser;
    }

    public String getOpenMrsMySqlPassword() {
        return openMrsMySqlPassword;
    }

    public String getOpenMrsMySqlDatabase() {
        return openMrsMySqlDatabase;
    }

    public int getOpenMrsMySqlPort() {
        return openMrsMySqlPort;
    }

    public boolean isTxRollback() {
        return txRollback;
    }
}
