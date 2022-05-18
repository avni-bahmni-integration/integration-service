package org.avni_integration_service.migrator.service;

import org.apache.log4j.Logger;
import org.avni_integration_service.bahmni.repository.intmapping.MappingService;
import org.avni_integration_service.integration_data.domain.*;
import org.avni_integration_service.integration_data.repository.*;
import org.avni_integration_service.migrator.repository.BahmniConfigurationRepository;
import org.avni_integration_service.util.FormatAndParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IntegrationDataService {
    @Autowired
    private BahmniConfigurationRepository implementationConfigurationRepository;
    @Autowired
    private ConstantsRepository constantsRepository;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;
    @Autowired
    private IgnoredIntegratingConceptRepository ignoredBahmniConceptRepository;
    @Autowired
    private AvniEntityStatusRepository avniEntityStatusRepository;
    @Autowired
    private ErrorRecordRepository errorRecordRepository;

    private static final Logger logger = Logger.getLogger(IntegrationDataService.class);

    public void createConstants() {
        Map<String, Object> constants = implementationConfigurationRepository.getConstants();
        List<Constant> list = constants.keySet().stream().filter(key -> constants.get(key) instanceof String).map(key -> new Constant(key, (String) constants.get(key))).collect(Collectors.toList());
        constants.keySet().stream().filter(key -> !(constants.get(key) instanceof String)).forEach(key -> {
            ArrayList arrayList = (ArrayList) constants.get(key);
            arrayList.forEach(valueElement -> {
                list.add(new Constant(key, (String) valueElement));
            });
        });
        constantsRepository.saveAll(list);
        logger.info("Created constants");
    }

    public void createStandardMappings() {
        List<Map<String, String>> standardMappings = implementationConfigurationRepository.getStandardMappings().getList();
        standardMappings.forEach(keyValues -> {
            mappingService.saveMapping(MappingGroup.valueOf(keyValues.get("MappingGroup")), MappingType.valueOf(keyValues.get("MappingType")), keyValues.get("Bahmni Value"), keyValues.get("Avni Value"));
        });
        logger.info("Standard mappings created in integration database");
    }

    public void cleanupMetadata() {
        ignoredBahmniConceptRepository.deleteAll();
        mappingMetaDataRepository.deleteAll();
        cleanupConstants();
        logger.info("Integration metadata cleaned up");
    }

    public void cleanupAvniToBahmniTxData() {
        Iterable<AvniEntityStatus> all = avniEntityStatusRepository.findAll();
        all.forEach(avniEntityStatus -> {
            avniEntityStatus.setReadUpto(FormatAndParseUtil.fromAvniDate("1900-01-01"));
            avniEntityStatusRepository.save(avniEntityStatus);
        });

        errorRecordRepository.findAllByAvniEntityTypeNotNull().forEach(errorRecord -> errorRecordRepository.delete(errorRecord));
    }

    public void cleanupConstants() {
        constantsRepository.deleteAll();
    }
}
