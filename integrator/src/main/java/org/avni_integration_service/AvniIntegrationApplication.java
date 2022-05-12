package org.avni_integration_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
public class AvniIntegrationApplication {
    private final RestTemplate restTemplate;

    @Autowired
	public AvniIntegrationApplication(Environment environment, RestTemplateBuilder restTemplateBuilder) {
        Duration timeout = Duration.ofSeconds(20);
        restTemplate = restTemplateBuilder.setConnectTimeout(timeout)
                .setReadTimeout(timeout)
                .build();
	}

	public static void main(String[] args) {
		SpringApplication.run(AvniIntegrationApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
        return restTemplate;
	}
}
