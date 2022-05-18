package org.avni_integration_service.avni;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@EntityScan(basePackages = {"org.avni_integration_service.util", "org.avni_integration_service.avni"})
@ComponentScan(basePackages = {"org.avni_integration_service.util", "org.avni_integration_service.avni"})
public class BaseAvniSpringTest {
}
