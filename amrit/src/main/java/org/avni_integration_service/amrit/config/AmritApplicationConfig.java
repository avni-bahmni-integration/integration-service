package org.avni_integration_service.amrit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AmritApplicationConfig {
    @Value("${amrit.server.url}")
    private String amritServerUrl;

    @Value("${amrit.api.user}")
    private String amritApiUser;

    @Value("${amrit.api.password}")
    private String amritApiPassword;

    public String getAmritServerUrl() {
        return amritServerUrl;
    }

    public String getAmritApiUser() {
        return amritApiUser;
    }

    public String getAmritApiPassword() {
        return amritApiPassword;
    }

    @Bean("AmritRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }
}
