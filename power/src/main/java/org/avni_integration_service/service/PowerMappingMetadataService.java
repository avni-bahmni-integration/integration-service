package org.avni_integration_service.service;

import org.avni_integration_service.avni.domain.Task;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.repository.IntegrationSystemRepository;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.util.MapUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

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
        return mappingMetaData == null ? null : mappingMetaData.getAvniValue();
    }

    public void addStateAndProgramToTaskMetadata(Task task, Map<String, Object> callResponse) {
        String toNumber = MapUtil.getString("To", callResponse);
        String state = getStateValueForMobileNumber(toNumber);
        String program = getProgramValueForMobileNumber(toNumber);
        if (state != null) {
            task.addMetadata("State", state);
        }
        if (program != null) {
            task.addMetadata("Program", program);
        }
    }

}
