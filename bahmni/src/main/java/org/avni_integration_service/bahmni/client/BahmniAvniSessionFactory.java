package org.avni_integration_service.bahmni.client;

import org.avni_integration_service.avni.client.AvniSession;
import org.avni_integration_service.avni.client.IdpType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BahmniAvniSessionFactory {
    @Value("${bahmni.avni.api.url}")
    private String apiUrl;

    @Value("${bahmni.avni.impl.username}")
    private String implUser;

    @Value("${bahmni.avni.impl.password}")
    private String implPassword;

    @Value("${bahmni.avni.authentication.enabled}")
    private boolean authEnabled;

    @Value("${bahmni.avni.idp.type}")
    private String idpType;

    public AvniSession createSession() {
        return new AvniSession(apiUrl, implUser, implPassword, authEnabled, IdpType.valueOf(idpType));
    }
}
