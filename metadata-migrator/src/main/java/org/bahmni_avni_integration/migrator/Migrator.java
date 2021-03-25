package org.bahmni_avni_integration.migrator;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.migrator.service.BahmniToAvniService;
import org.bahmni_avni_integration.migrator.service.IntegrationDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.sql.SQLException;

@SpringBootApplication
@EnableJpaRepositories("org.bahmni_avni_integration.integration_data.repository")
@EntityScan(basePackages = {"org.bahmni_avni_integration.integration_data.*", "org.bahmni_avni_integration.integration_data.domain.*"})
public class Migrator implements CommandLineRunner {
    @Autowired
    private BahmniToAvniService bahmniToAvniService;
    @Autowired
    private IntegrationDataService integrationDataService;

    private static Logger logger = Logger.getLogger(Migrator.class);

    public static void main(String[] args) throws SQLException {
        SpringApplication.run(Migrator.class, args).close();
    }

    @Override
    public void run(String... args) throws Exception {
        if (args.length == 0) return;

        try {
            bahmniToAvniService.cleanup();
            integrationDataService.cleanup();

            bahmniToAvniService.createStandardMetadata();
            bahmniToAvniService.migratePatientAttributes();
            bahmniToAvniService.migrateConcepts();
            bahmniToAvniService.migrateForms();

            integrationDataService.createConstants();
            integrationDataService.createStandardMappings();
        } catch (Exception e) {
            logger.error("Migrator failed", e);
            System.exit(1);
        } finally {
            System.exit(0);
        }
    }
}