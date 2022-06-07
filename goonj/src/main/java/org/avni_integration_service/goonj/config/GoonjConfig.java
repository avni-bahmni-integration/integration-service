package org.avni_integration_service.goonj.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
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

    @Value("${goonj.sf.appUrl}")
    private String appUrl;

    public String getAppUrl() {
        return appUrl;
    }

    @Bean
    @Qualifier("goonjRestTemplate")
    RestTemplate goonjRestTemplate(OAuth2AuthorizedClientService clientService) {
        return new RestTemplateBuilder()
                .interceptors((ClientHttpRequestInterceptor) (httpRequest, bytes, execution) -> {

                    OAuth2AuthenticationToken token = OAuth2AuthenticationToken.class.cast(
                            SecurityContextHolder.getContext().getAuthentication());

                    OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                            token.getAuthorizedClientRegistrationId(),
                            token.getName());

                    httpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());

                    return execution.execute(httpRequest, bytes);
                })
                .build();
    }
}
