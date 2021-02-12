package org.bahmni_avni_integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BahmniAvniIntegrationApplication {

	private final Environment environment;

	@Autowired
	public BahmniAvniIntegrationApplication(Environment environment) {
		this.environment = environment;
	}

	public static void main(String[] args) {
		SpringApplication.run(BahmniAvniIntegrationApplication.class, args);
	}

	@Bean
	public Boolean isTest() {
		String[] activeProfiles = environment.getActiveProfiles();
		return activeProfiles.length == 1 && activeProfiles[0].equals("test");
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		return restTemplate;
	}
}
