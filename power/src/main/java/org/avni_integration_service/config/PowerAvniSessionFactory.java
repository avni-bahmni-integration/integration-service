package org.avni_integration_service.config;

import org.avni_integration_service.avni.client.AvniSession;
import org.avni_integration_service.avni.client.IdpType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PowerAvniSessionFactory {
    @Value("${power.avni.api.url}")
    private String apiUrl;

    @Value("${power.avni.impl.username}")
    private String implUser;

    @Value("${power.avni.impl.password}")
    private String implPassword;

    @Value("${power.avni.authentication.enabled}")
    private boolean authEnabled;

    public AvniSession createSession() {
        return new AvniSession(apiUrl, implUser, implPassword, authEnabled);
    }
}
