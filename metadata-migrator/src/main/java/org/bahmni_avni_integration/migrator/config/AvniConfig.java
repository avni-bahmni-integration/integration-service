package org.bahmni_avni_integration.migrator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvniConfig {
    @Value("${avni.postgres.user}")
    private String avniPostgresUser;

    @Value("${avni.postgres.password}")
    private String avniPostgresPassword;

    @Value("${avni.postgres.database}")
    private String avniPostgresDatabase;

    @Value("${avni.server.ssh.local.port}")
    private int localPort;

    public String getAvniPostgresUser() {
        return avniPostgresUser;
    }

    public String getAvniPostgresPassword() {
        return avniPostgresPassword;
    }

    public String getAvniPostgresDatabase() {
        return avniPostgresDatabase;
    }

    public int getLocalPort() {
        return localPort;
    }
}