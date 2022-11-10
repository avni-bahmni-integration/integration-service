package org.avni_integration_service.amrit.config;

import org.avni_integration_service.amrit.service.AmritTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AmritWebConfig {
    private final AmritTokenService amritTokenService;

    public AmritWebConfig(AmritTokenService amritTokenService) {
        this.amritTokenService = amritTokenService;
    }


    @Bean("AmritRestTemplate")
    RestTemplate restTemplate() {

        return new RestTemplateBuilder()
                .interceptors((httpRequest, bytes, execution) -> {
                    httpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, amritTokenService.getRefreshedToken());
                    httpRequest.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json");
                    return execution.execute(httpRequest, bytes);
                })
                .build();
    }
}
