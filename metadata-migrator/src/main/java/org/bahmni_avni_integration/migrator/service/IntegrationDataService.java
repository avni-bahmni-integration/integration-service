package org.bahmni_avni_integration.migrator.service;

import org.bahmni_avni_integration.integration_data.domain.Constant;
import org.bahmni_avni_integration.integration_data.domain.ConstantKey;
import org.bahmni_avni_integration.integration_data.domain.MappingGroup;
import org.bahmni_avni_integration.integration_data.domain.MappingType;
import org.bahmni_avni_integration.integration_data.repository.ConstantsRepository;
import org.bahmni_avni_integration.integration_data.repository.MappingMetaDataRepository;
import org.bahmni_avni_integration.migrator.repository.ImplementationConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void createConstants() {
        Map<String, String> constants = implementationConfigurationRepository.getConstants();
        List<Constant> list = constants.keySet().stream().map(key -> new Constant(ConstantKey.valueOf(key), constants.get(key))).collect(Collectors.toList());
        constantsRepository.saveAll(list);
    }

    public void createStandardMappings() {
        List<Map<String, String>> standardMappings = implementationConfigurationRepository.getStandardMappings();
        standardMappings.forEach(keyValues -> {
            mappingMetaDataRepository.saveMapping(MappingGroup.valueOf(keyValues.get("MappingGroup")), MappingType.valueOf(keyValues.get("MappingType")), keyValues.get("Bahmni Value"), keyValues.get("Avni Value"));
        });
    }
}