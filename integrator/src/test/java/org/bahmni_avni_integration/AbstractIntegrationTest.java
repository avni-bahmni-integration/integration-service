package org.bahmni_avni_integration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"org.bahmni_avni_integration.integration_data.domain", "org.bahmni_avni_integration.entity"})
@EnableJpaRepositories(basePackages = {"org.bahmni_avni_integration.integration_data.repository", "org.bahmni_avni_integration.repository", "org.bahmni_avni_integration.integration_data.repository"})
public class AbstractIntegrationTest {
}
