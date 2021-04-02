package org.bahmni_avni_integration.integration.data.repository;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@EntityScan(basePackages = {"org.bahmni_avni_integration.integration_data.domain"})
@EnableJpaRepositories(basePackages = "org.bahmni_avni_integration.integration_data.repository")
public abstract class AbstractRepositoryTest {
}