package org.avni_integration_service.amrit.config;

import org.avni_integration_service.avni.client.AvniSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AmritAvniSessionFactory {
    @Value("${amrit.avni.api.url}")
    private String apiUrl;

    @Value("${amrit.avni.impl.username}")
    private String implUser;

    @Value("${amrit.avni.impl.password}")
    private String implPassword;

    @Value("${amrit.avni.authentication.enabled}")
    private boolean authEnabled;

    public AvniSession createSession() {
        return new AvniSession(apiUrl, implUser, implPassword, authEnabled);
    }
}
