package org.avni_integration_service.integration_data.config;

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
}
