package org.avni_integration_service.integration_data.repository;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@EntityScan(basePackages = {"org.avni_integration_service.integration_data.domain"})
@EnableJpaRepositories(basePackages = "org.avni_integration_service.integration_data.repository")
@ComponentScan(basePackages = "org.avni_integration_service.integration_data")
public abstract class AbstractSpringTest {
}
