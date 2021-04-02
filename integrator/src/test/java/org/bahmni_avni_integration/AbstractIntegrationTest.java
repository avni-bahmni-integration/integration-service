package org.bahmni_avni_integration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"org.bahmni_avni_integration.integration_data.domain"})
@EnableJpaRepositories(basePackages = "org.bahmni_avni_integration.integration_data.repository")
public class AbstractIntegrationTest {
}