package org.bahmni_avni_integration.migrator;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.migrator.service.AvniToBahmniService;
import org.bahmni_avni_integration.migrator.service.BahmniToAvniService;
import org.bahmni_avni_integration.migrator.service.IntegrationDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableJpaRepositories("org.bahmni_avni_integration.integration_data.repository")
@EntityScan(basePackages = {"org.bahmni_avni_integration.integration_data.*", "org.bahmni_avni_integration.integration_data.domain.*"})
public class Migrator implements CommandLineRunner {
    private final BahmniToAvniService bahmniToAvniService;
    private final IntegrationDataService integrationDataService;
    private final AvniToBahmniService avniToBahmniService;

    private static final Logger logger = Logger.getLogger(Migrator.class);

    public Migrator(BahmniToAvniService bahmniToAvniService, IntegrationDataService integrationDataService, AvniToBahmniService avniToBahmniService) {
        this.bahmniToAvniService = bahmniToAvniService;
        this.integrationDataService = integrationDataService;
        this.avniToBahmniService = avniToBahmniService;
    }

    public static void main(String[] args) throws SQLException {
        SpringApplication.run(Migrator.class, args).close();
    }

    @Override
    public void run(String... args) throws Exception {
        List<String> nonSpringArguments = Arrays.stream(args).filter(s -> !s.startsWith("--")).collect(Collectors.toList());
        if (nonSpringArguments.size() == 0) return;
        switch (nonSpringArguments.get(0)) {
            case "adhoc" -> {
                runAdhoc();
            }
            case "bahmni-to-avni" -> {
                bahmniToAvni();
            }
            case "avni-to-bahmni" -> {
                avniToBahmni();
            }
        }

    }

    private void avniToBahmni() {
        logger.debug("Migrating metadata from Avni to Bahmni");
        System.exit(0);
    }

    private void bahmniToAvni() {
        try {
            bahmniToAvniService.cleanup();
            integrationDataService.cleanup();

            bahmniToAvniService.migratePatientAttributes();
            bahmniToAvniService.migrateConcepts();
            bahmniToAvniService.createStandardMetadata();
            bahmniToAvniService.migrateForms();

            integrationDataService.createConstants();
            integrationDataService.createStandardMappings();
            System.exit(0);
        } catch (Exception e) {
            logger.error("Migrator failed", e);
            System.exit(1);
        }
    }

    private void runAdhoc() {
        integrationDataService.cleanupConstants();
        integrationDataService.createConstants();
        System.exit(0);
    }
}