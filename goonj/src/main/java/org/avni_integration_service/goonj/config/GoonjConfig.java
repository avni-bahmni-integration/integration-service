package org.avni_integration_service.goonj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:/goonj-secret.properties")
public class GoonjConfig {
    @Value("${goonj.sf.authUrl}")
    private String salesForceAuthUrl;

    @Value("${goonj.sf.userName}")
    private String loginUserName;

    @Value("${goonj.sf.password}")
    private String loginPassword;

    @Value("${goonj.sf.clientId}")
    private String clientId;

    @Value("${goonj.sf.clientSecret}")
    private String clientSecret;

    @Value("${goonj.sf.appUrl}")
    private String appUrl;

    public String getSalesForceAuthUrl() {
        return salesForceAuthUrl;
    }

    public String getLoginUserName() {
        return loginUserName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAppUrl() {
        return appUrl;
    }
}
