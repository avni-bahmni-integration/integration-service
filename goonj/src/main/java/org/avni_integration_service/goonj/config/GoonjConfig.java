package org.avni_integration_service.goonj.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
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

    @Autowired
    private SalesForceUserRepository salesForceUserRepository;

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

    @Bean("GoonjRestTemplate")
    RestTemplate restTemplate(OAuth2AuthorizedClientService clientService) {
        return new RestTemplateBuilder()
                .interceptors((ClientHttpRequestInterceptor) (httpRequest, bytes, execution) -> {
                //TODO , do token fetch only on first attempt or if it has expired

                httpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + salesForceUserRepository
                        .login().getAccessToken());

                    return execution.execute(httpRequest, bytes);
                })
                .build();
    }
}
