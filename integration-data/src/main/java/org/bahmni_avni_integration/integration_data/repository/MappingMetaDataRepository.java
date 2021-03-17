package org.bahmni_avni_integration.integration_data.repository;

import org.bahmni_avni_integration.integration_data.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MappingMetaDataRepository extends PagingAndSortingRepository<MappingMetaData, Integer> {
    MappingMetaData findByMappingGroupAndMappingType(MappingGroup mappingGroup, MappingType mappingType);

    MappingMetaData findByMappingGroupAndMappingTypeAndBahmniValue(MappingGroup mappingGroup, MappingType mappingType, String bahmniValue);

    List<MappingMetaData> findAllByMappingGroupAndMappingType(MappingGroup mappingGroup, MappingType mappingType);

    List<MappingMetaData> findAllByMappingGroupAndMappingTypeIn(MappingGroup mappingGroup, List<MappingType> mappingTypes);

    List<MappingMetaData> findAllByMappingType(MappingType mappingType);

    default MappingMetaDataCollection findAll(MappingGroup mappingGroup, List<MappingType> mappingTypes) {
        return new MappingMetaDataCollection(findAllByMappingGroupAndMappingTypeIn(mappingGroup, mappingTypes));
    }

    default MappingMetaDataCollection findAll(MappingGroup mappingGroup, MappingType mappingType) {
        return new MappingMetaDataCollection(findAllByMappingGroupAndMappingType(mappingGroup, mappingType));
    }

    default String getAvniValue(MappingGroup mappingGroup, MappingType mappingType) {
        MappingMetaData mapping = findByMappingGroupAndMappingType(mappingGroup, mappingType);
        return getAvniValue(mapping);
    }

    private String getAvniValue(MappingMetaData mapping) {
        if (mapping == null) return null;
        return mapping.getAvniValue();
    }

    default String getBahmniValue(MappingGroup mappingGroup, MappingType mappingType) {
        MappingMetaData mapping = findByMappingGroupAndMappingType(mappingGroup, mappingType);
        if (mapping == null) return null;
        return mapping.getBahmniValue();
    }

    default MappingMetaData getConceptMappingByOpenMRSConcept(String openMRSConceptUuid) {
        MappingMetaData mappingMetaData = findByMappingGroupAndMappingTypeAndBahmniValue(MappingGroup.Observation, MappingType.Concept, openMRSConceptUuid);
        if (mappingMetaData == null)
            throw new RuntimeException(String.format("No mapping found for openmrs concept with uuid = %s", openMRSConceptUuid));
        return mappingMetaData;
    }

    default MappingMetaData saveMapping(MappingGroup mappingGroup, MappingType mappingType, String bahmniValue, String avniValue, ObsDataType obsDataType) {
        MappingMetaData mappingMetaData = new MappingMetaData();
        mappingMetaData.setMappingGroup(mappingGroup);
        mappingMetaData.setMappingType(mappingType);
        mappingMetaData.setBahmniValue(bahmniValue);
        mappingMetaData.setAvniValue(avniValue);
        mappingMetaData.setDataTypeHint(obsDataType);
        return save(mappingMetaData);
    }
}
