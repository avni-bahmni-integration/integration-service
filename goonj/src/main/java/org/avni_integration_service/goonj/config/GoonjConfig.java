package org.avni_integration_service.goonj.config;

import org.avni_integration_service.goonj.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
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

    @Value("${goonj.sf.tokenExpiry}")
    private int tokenExpiry;

    @Autowired
    private TokenService tokenService;

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

    public int getTokenExpiry() {
        return tokenExpiry;
    }

    @Bean("GoonjRestTemplate")
    RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .interceptors((ClientHttpRequestInterceptor) (httpRequest, bytes, execution) -> {
                    httpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION,
                            "Bearer " + tokenService.getRefreshedToken().getTokenValue());
                    return execution.execute(httpRequest, bytes);
                })
                .build();
    }
}
