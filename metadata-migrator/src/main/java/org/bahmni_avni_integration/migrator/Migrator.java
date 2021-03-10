package org.bahmni_avni_integration.migrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("org.bahmni_avni_integration.integration_data.repository")
@EntityScan(basePackages = { "org.bahmni_avni_integration.integration_data.*", "org.bahmni_avni_integration.integration_data.domain.*"})
public class Migrator {
    public static void main(String[] args) {
        SpringApplication.run(Migrator.class, args);
    }
}