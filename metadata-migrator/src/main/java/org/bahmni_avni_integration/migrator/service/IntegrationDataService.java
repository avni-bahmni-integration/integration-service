package org.bahmni_avni_integration.migrator.service;

import org.apache.log4j.Logger;
import org.bahmni_avni_integration.integration_data.domain.Constant;
import org.bahmni_avni_integration.integration_data.domain.ConstantKey;
import org.bahmni_avni_integration.integration_data.domain.MappingGroup;
import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.migrator.repository.ImplementationConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IntegrationDataService {
    @Autowired
    private ImplementationConfigurationRepository implementationConfigurationRepository;
    @Autowired
    private ConstantsRepository constantsRepository;
    @Autowired
    private MappingMetaDataRepository mappingMetaDataRepository;

    private static final Logger logger = Logger.getLogger(IntegrationDataService.class);

    public void createConstants() {
        Map<String, Object> constants = implementationConfigurationRepository.getConstants();
        List<Constant> list = constants.keySet().stream().filter(key -> constants.get(key) instanceof String).map(key -> new Constant(ConstantKey.valueOf(key), (String) constants.get(key))).collect(Collectors.toList());
        constants.keySet().stream().filter(key -> !(constants.get(key) instanceof String)).forEach(key -> {
            ArrayList arrayList = (ArrayList) constants.get(key);
            arrayList.forEach(valueElement -> {
                list.add(new Constant(ConstantKey.valueOf(key), (String) valueElement));
            });
        });
        constantsRepository.saveAll(list);
        logger.info("Created constants");
    }

    public void createStandardMappings() {
        List<Map<String, String>> standardMappings = implementationConfigurationRepository.getStandardMappings().getList();
        standardMappings.forEach(keyValues -> {
            mappingMetaDataRepository.saveMapping(MappingGroup.valueOf(keyValues.get("MappingGroup")), MappingType.valueOf(keyValues.get("MappingType")), keyValues.get("Bahmni Value"), keyValues.get("Avni Value"));
        });
        logger.info("Standard mappings created in integration database");
    }

    public void cleanup() {
        mappingMetaDataRepository.deleteAll();
        constantsRepository.deleteAll();
        logger.info("Integration metadata cleaned up");
    }
}