package org.avni_integration_service.avni.config;

import org.springframework.beans.factory.annotation.Value;
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
}
