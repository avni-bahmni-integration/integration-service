package org.bahmni_avni_integration.migrator.config;

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

    @Value("${openmrs.refdata.admin.id}")
    private int refDataAdminId;

    @Value("${openmrs.txdata.admin.id}")
    private int txDataAdminId;


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

    public int getRefDataAdminId() {
        return refDataAdminId;
    }

    public int getTxDataAdminId() {
        return txDataAdminId;
    }
}