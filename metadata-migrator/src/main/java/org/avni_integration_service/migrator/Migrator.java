package org.avni_integration_service.migrator;

import org.apache.log4j.Logger;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.migrator.service.AvniToBahmniService;
import org.avni_integration_service.migrator.service.BahmniToAvniService;
import org.avni_integration_service.migrator.service.IntegrationDataService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableJpaRepositories("org.avni_integration_service.integration_data.repository")
@EntityScan(basePackages = {"org.avni_integration_service.integration_data.*", "org.avni_integration_service.integration_data.domain.*"})
@ComponentScan(basePackages = {"org.avni_integration_service.integration_data", "org.avni_integration_service.migrator.*"})
public class Migrator implements CommandLineRunner {
    private final BahmniToAvniService bahmniToAvniService;
    private final IntegrationDataService integrationDataService;
    private final AvniToBahmniService avniToBahmniService;
    private final MappingMetaDataRepository mappingMetaDataRepository;

    private static final Logger logger = Logger.getLogger(Migrator.class);

    public Migrator(BahmniToAvniService bahmniToAvniService, IntegrationDataService integrationDataService, AvniToBahmniService avniToBahmniService, MappingMetaDataRepository mappingMetaDataRepository, MappingMetaDataRepository mappingMetaDataRepository1) {
        this.bahmniToAvniService = bahmniToAvniService;
        this.integrationDataService = integrationDataService;
        this.avniToBahmniService = avniToBahmniService;
        this.mappingMetaDataRepository = mappingMetaDataRepository1;
    }

    public static void main(String[] args) {
        SpringApplication.run(Migrator.class, args).close();
    }

    @Override
    public void run(String... args) {
        try {
            List<String> nonSpringArguments = Arrays.stream(args).filter(s -> !s.startsWith("--")).collect(Collectors.toList());
            if (nonSpringArguments.size() == 0) return;
            switch (MigratorDirection.valueOf(nonSpringArguments.get(0))) {
                case Adhoc -> {
                    runAdhoc();
                }
                case AvniToBahmni -> {
                    avniToBahmni();
                }
                case BahmniToAvni -> {
                    bahmniToAvni();
                }
            }
        } catch (Exception e) {
            logger.error("Migrator failed", e);
            System.exit(1);
        }
        System.exit(0);
    }

    private void avniToBahmni() throws SQLException {
        logger.debug("Migrating metadata from Avni to Bahmni");
        avniToBahmniService.migrate();

        integrationDataService.createConstants();
        integrationDataService.createStandardMappings();
    }

    private void bahmniToAvni() throws SQLException {
        bahmniToAvniService.migratePatientAttributes();
        bahmniToAvniService.migrateConcepts();
        bahmniToAvniService.createStandardMetadata();
        bahmniToAvniService.migrateForms();

        integrationDataService.createConstants();
        integrationDataService.createStandardMappings();
    }

    private void runAdhoc() throws SQLException {
        bahmniToAvniService.createOrUpdateConceptMapping();
    }
}
