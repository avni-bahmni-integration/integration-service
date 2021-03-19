package org.bahmni_avni_integration.migrator;

import org.bahmni_avni_integration.migrator.service.BahmniToAvniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@SpringBootApplication
@EnableJpaRepositories("org.bahmni_avni_integration.integration_data.repository")
@EntityScan(basePackages = { "org.bahmni_avni_integration.integration_data.*", "org.bahmni_avni_integration.integration_data.domain.*"})
public class Migrator implements CommandLineRunner {
    @Autowired
    private BahmniToAvniService bahmniToAvniService;

    public static void main(String[] args) throws SQLException {
        SpringApplication.run(Migrator.class, args).close();
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) return;
        if (args[0].equals("run")) {
            bahmniToAvniService.migratePatientAttributes();
            bahmniToAvniService.migrateForms();
        }
    }
}