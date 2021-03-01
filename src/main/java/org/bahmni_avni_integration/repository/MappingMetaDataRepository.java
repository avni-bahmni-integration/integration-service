package org.bahmni_avni_integration.repository;

import org.bahmni_avni_integration.domain.MappingGroup;
import org.bahmni_avni_integration.domain.MappingMetaData;
import org.bahmni_avni_integration.domain.MappingMetaDataCollection;
import org.bahmni_avni_integration.domain.MappingType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MappingMetaDataRepository extends PagingAndSortingRepository<MappingMetaData, Integer> {
    MappingMetaData findByMappingGroupAndMappingType(MappingGroup mappingGroup, MappingType mappingType);

    MappingMetaData findByMappingGroupAndMappingTypeAndBahmniValue(MappingGroup mappingGroup, MappingType mappingType, String bahmniValue);

    List<MappingMetaData> findAllByMappingGroupAndMappingType(MappingGroup mappingGroup, MappingType mappingType);

    List<MappingMetaData> findAllByMappingGroupAndMappingTypeIn(MappingGroup mappingGroup, List<MappingType> mappingTypes);

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
}
