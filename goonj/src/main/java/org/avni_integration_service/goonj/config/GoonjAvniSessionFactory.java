package org.avni_integration_service.goonj.config;

import org.avni_integration_service.avni.client.AvniSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoonjAvniSessionFactory {
    @Value("${goonj.avni.api.url}")
    private String apiUrl;

    @Value("${goonj.avni.impl.username}")
    private String implUser;

    @Value("${goonj.avni.impl.password}")
    private String implPassword;

    @Value("${goonj.avni.authentication.enabled}")
    private boolean authEnabled;

    @Bean("GoonjAvniSession")
    public AvniSession createSession() {
        return new AvniSession(apiUrl, implUser, implPassword, authEnabled);
    }
}
