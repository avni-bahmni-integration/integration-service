package org.avni_integration_service;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"org.avni_integration_service.integration_data.domain", "org.avni_integration_service.entity"})
@EnableJpaRepositories(basePackages = {"org.avni_integration_service.integration_data.repository", "org.avni_integration_service.repository", "org.avni_integration_service.integration_data.repository"})
@ComponentScan(basePackages = {"org.avni_integration_service"})
public class AbstractIntegrationTest {
}
