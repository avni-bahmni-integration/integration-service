package org.avni_integration_service.amrit;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"org.avni_integration_service.amrit", "org.avni_integration_service.integration_data", "org.avni_integration_service.avni"})
@EntityScan(basePackages = {"org.avni_integration_service.amrit", "org.avni_integration_service.integration_data", "org.avni_integration_service.avni"})
@EnableAutoConfiguration
@ComponentScan(basePackages = {"org.avni_integration_service.amrit", "org.avni_integration_service.integration_data", "org.avni_integration_service.avni", "org.avni_integration_service.util"})
public abstract class BaseAmritSpringTest {
}
