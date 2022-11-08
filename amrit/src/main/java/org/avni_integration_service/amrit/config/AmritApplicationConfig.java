package org.avni_integration_service.amrit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Component
public class AmritApplicationConfig {
    @Value("${amrit.server.url}")
    private String amritServerUrl;

    @Value("${amrit.api.user}")
    private String amritApiUser;

    @Value("${amrit.api.password}")
    private String amritApiPassword;

    @Value("${amrit.api.common.prefix:commonapi-v1.1}")
    private String commonApiPrefix;


    @Value("${amrit.api.tm.prefix:tmapi-v1.0}")
    private String tmApiPrefix;


    @Value("${amrit.api.identity.prefix:identity-0.0.1.1}")
    private String identityApiPrefix;


    public String getAmritServerUrl() {
        return amritServerUrl;
    }

    public String getCommonApiPrefix() {
        return commonApiPrefix;
    }

    public String getTmApiPrefix() {
        return tmApiPrefix;
    }

    public String getIdentityApiPrefix() {
        return identityApiPrefix;
    }

    public String getAmritApiUser() {
        return amritApiUser;
    }

    public String getAmritApiPassword() {
        return amritApiPassword;
    }


    @Bean("AmritRestTemplate")
    RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .interceptors((httpRequest, bytes, execution) -> {
//                    String authorizationEncoding = Base64.getEncoder().encodeToString(
//                            (String.format("%s:%s", this.amritAPIKey, this.amritAPIToken)).getBytes()
//                    );
//                    httpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, "Basic " + authorizationEncoding);
                    httpRequest.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
                    return execution.execute(httpRequest, bytes);
                })
                .build();
    }
}
