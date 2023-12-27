package org.bahmni_avni_integration.integration_data.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AvniConfig {
    @Value("${avni.db.user}")
    private String avniPostgresUser;

    @Value("${avni.db.password}")
    private String avniPostgresPassword;

    @Value("${avni.db.name}")
    private String avniPostgresDatabase;

    @Value("${avni.db.port}")
    private int dbPort;

    @Value("${avni.impl_org.db.user}")
    private String implementationOrgDbUser;

    @Value("${avni.impl.user.id}")
    private int implementationUserId;

    @Value("${app.httpClient.timeout}")
    private int httpClientTimeout;

    public String getAvniPostgresUser() {
        return avniPostgresUser;
    }

    public String getAvniPostgresPassword() {
        return avniPostgresPassword;
    }

    public String getAvniPostgresDatabase() {
        return avniPostgresDatabase;
    }

    public int getDbPort() {
        return dbPort;
    }

    public String getImplementationOrgDbUser() {
        return implementationOrgDbUser;
    }

    public int getImplementationUserId() {
        return implementationUserId;
    }

    public int getHttpClientTimeout() {
        return httpClientTimeout;
    }
}
