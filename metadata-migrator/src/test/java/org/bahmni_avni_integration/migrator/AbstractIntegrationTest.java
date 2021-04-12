package org.bahmni_avni_integration.migrator;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@EntityScan(basePackages = {"org.bahmni_avni_integration.integration_data.domain", "org.bahmni_avni_integration.entity"})
@EnableJpaRepositories(basePackages = {"org.bahmni_avni_integration.integration_data.repository", "org.bahmni_avni_integration.integration_data.repository"})
public class AbstractIntegrationTest {
}