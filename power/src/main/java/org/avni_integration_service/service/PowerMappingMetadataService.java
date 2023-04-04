package org.avni_integration_service.service;

import org.avni_integration_service.avni.domain.Task;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PowerMappingMetadataService {

    private final MappingMetaDataRepository mappingMetaDataRepository;

    private final IntegrationSystemRepository integrationSystemRepository;

    public PowerMappingMetadataService(MappingMetaDataRepository mappingMetaDataRepository,
                                       IntegrationSystemRepository integrationSystemRepository) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
        this.integrationSystemRepository = integrationSystemRepository;
    }

    public String getStateValueForMobileNumber(String mobileNumber) {
        return getAvniValueByMappingTypeAndIntValueForMobileNumberGroup("State", mobileNumber);
    }

    public String getProgramValueForMobileNumber(String mobileNumber) {
        return getAvniValueByMappingTypeAndIntValueForMobileNumberGroup("Program", mobileNumber);
    }

    private String getAvniValueByMappingTypeAndIntValueForMobileNumberGroup(String mappingType, String intSystemValue) {
        MappingMetaData mappingMetaData = mappingMetaDataRepository.getAvniMappingIfPresent(
                "PhoneNumber",
                mappingType,
                intSystemValue,
                integrationSystemRepository.findByName("power")
        );
        if(mappingMetaData == null) {
            throw new RuntimeException(String.format("Unable to find %s mapping for %s phoneNumber",
                    mappingType, intSystemValue));
        }
        return mappingMetaData.getAvniValue();
    }

    public void addStateAndProgramToTaskMetadata(Task task, String state, String program) {
        if (state != null) {
            task.addMetadata("State", state);
        }
        if (program != null) {
            task.addMetadata("Program", program);
        }
    }

    public Set<String> findAllCallPhoneNumbers() {
        List<MappingMetaData> mappingMetaDataList = mappingMetaDataRepository.findAllByMappingGroupNameAndIntegrationSystem(
                "PhoneNumber",
                integrationSystemRepository.findByName("power")
        );
        return mappingMetaDataList == null ? Collections.emptySet() : mappingMetaDataList.stream()
                .map(MappingMetaData::getIntSystemValue).collect(Collectors.toSet());
    }

}
