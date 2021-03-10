package org.bahmni_avni_integration.migrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationConfig {
    @Value("${integration.db.user}")
    private String dbUser;

    @Value("${integration.db.password}")
    private String dbPassword;

    @Value("${integration.db.name}")
    private String dbName;

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbName() {
        return dbName;
    }
}