package org.avni_integration_service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class BeanConfiguration {
    private final RestTemplate restTemplate;

    @Autowired
    public BeanConfiguration(Environment environment, RestTemplateBuilder restTemplateBuilder) {
        Duration timeout = Duration.ofSeconds(20);
        restTemplate = restTemplateBuilder.setConnectTimeout(timeout)
                .setReadTimeout(timeout)
                .build();
    }

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return restTemplate;
    }
}
