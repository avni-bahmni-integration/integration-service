package org.avni_integration_service.bahmni.repository.intmapping;

import org.avni_integration_service.bahmni.MappingMetaDataCollection;
import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.avni_integration_service.integration_data.repository.MappingMetaDataRepository;
import org.avni_integration_service.util.ObsDataType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MappingService {
    private final MappingMetaDataRepository mappingMetaDataRepository;

    @Autowired
    public MappingService(MappingMetaDataRepository mappingMetaDataRepository) {
        this.mappingMetaDataRepository = mappingMetaDataRepository;
    }

    public MappingMetaDataCollection findAll(MappingGroup mappingGroup, List<MappingType> mappingTypes) {
        return new MappingMetaDataCollection(mappingMetaDataRepository.findAllByMappingGroupAndMappingTypeIn(mappingGroup, mappingTypes));
    }

    public MappingMetaDataCollection findAll(MappingGroup mappingGroup, MappingType mappingType) {
        return new MappingMetaDataCollection(mappingMetaDataRepository.findAllByMappingGroupAndMappingType(mappingGroup, mappingType));
    }

    public String getAvniValue(MappingGroup mappingGroup, MappingType mappingType) {
        MappingMetaData mapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(mappingGroup, mappingType);
        return getAvniValue(mapping);
    }

    private String getAvniValue(MappingMetaData mapping) {
        if (mapping == null) return null;
        return mapping.getAvniValue();
    }

    public String getBahmniValue(MappingGroup mappingGroup, MappingType mappingType) {
        MappingMetaData mapping = mappingMetaDataRepository.findByMappingGroupAndMappingType(mappingGroup, mappingType);
        if (mapping == null) return null;
        return mapping.getIntSystemValue();
    }

    public String getBahmniValue(MappingGroup mappingGroup, MappingType mappingType, String avniValue) {
        MappingMetaData mapping = mappingMetaDataRepository.findByMappingGroupAndMappingTypeAndAvniValue(mappingGroup, mappingType, avniValue);
        if (mapping == null) return null;
        return mapping.getIntSystemValue();
    }

    public MappingMetaData saveMapping(MappingGroup mappingGroup, MappingType mappingType, String bahmniValue, String avniValue, ObsDataType obsDataType) {
        MappingMetaData mappingMetaData = createMappingMetaData(mappingGroup, mappingType, bahmniValue, avniValue);
        mappingMetaData.setDataTypeHint(obsDataType);
        return mappingMetaDataRepository.save(mappingMetaData);
    }

    private MappingMetaData createMappingMetaData(MappingGroup mappingGroup, MappingType mappingType, String bahmniValue, String avniValue) {
        MappingMetaData mappingMetaData = new MappingMetaData();
        mappingMetaData.setMappingGroup(mappingGroup);
        mappingMetaData.setMappingType(mappingType.name());
        mappingMetaData.setIntSystemValue(bahmniValue);
        mappingMetaData.setAvniValue(avniValue);
        return mappingMetaData;
    }

    public MappingMetaData saveMapping(MappingGroup mappingGroup, MappingType mappingType, String bahmniValue, String avniValue) {
        MappingMetaData mappingMetaData = createMappingMetaData(mappingGroup, mappingType, bahmniValue, avniValue);
        return mappingMetaDataRepository.save(mappingMetaData);
    }

    public String getBahmniValueForAvniIdConcept() {
        return getBahmniValue(MappingGroup.Common, MappingType.AvniUUID_Concept);
    }

    public String getBahmniFormUuidForProgramEncounter(String encounterType) {
        return getBahmniValue(MappingGroup.ProgramEncounter,
                MappingType.CommunityProgramEncounter_BahmniForm,
                encounterType);
    }

    public String getBahmniFormUuidForGeneralEncounter(String encounterType) {
        return getBahmniValue(MappingGroup.GeneralEncounter,
                MappingType.CommunityEncounter_BahmniForm,
                encounterType);
    }

    public MappingMetaData findByMappingGroupAndMappingType(MappingGroup patientSubject, MappingType mappingType) {
        return mappingMetaDataRepository.findByMappingGroupAndMappingType(patientSubject, mappingType);
    }
}
