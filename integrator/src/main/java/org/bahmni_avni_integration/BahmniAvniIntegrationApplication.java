package org.bahmni_avni_integration;

import org.bahmni_avni_integration.integration_data.config.AvniConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
public class BahmniAvniIntegrationApplication {
    private final RestTemplate restTemplate;

    @Autowired
	public BahmniAvniIntegrationApplication(RestTemplateBuilder restTemplateBuilder, AvniConfig avniConfig) {
        Duration timeout = Duration.ofSeconds(avniConfig.getHttpClientTimeout());
        restTemplate = restTemplateBuilder.setConnectTimeout(timeout)
                .setReadTimeout(timeout)
                .build();
	}

	public static void main(String[] args) {
		SpringApplication.run(BahmniAvniIntegrationApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
        return restTemplate;
	}
}
