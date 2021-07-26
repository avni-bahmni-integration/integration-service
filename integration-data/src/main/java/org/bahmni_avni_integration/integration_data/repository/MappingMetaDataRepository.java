package org.bahmni_avni_integration.integration_data.repository;

import org.bahmni_avni_integration.integration_data.domain.*;
import org.bahmni_avni_integration.integration_data.internal.BahmniEncounterToAvniEncounterMetaData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MappingMetaDataRepository extends PagingAndSortingRepository<MappingMetaData, Integer> {
    MappingMetaData findByMappingGroupAndMappingType(MappingGroup mappingGroup, MappingType mappingType);

    MappingMetaData findByMappingGroupAndMappingTypeAndBahmniValue(MappingGroup mappingGroup, MappingType mappingType, String bahmniValue);

    MappingMetaData findByMappingGroupAndMappingTypeAndAvniValue(MappingGroup mappingGroup, MappingType mappingType, String avniValue);

    List<MappingMetaData> findAllByMappingGroupAndMappingType(MappingGroup mappingGroup, MappingType mappingType);

    List<MappingMetaData> findAllByMappingGroupAndMappingTypeIn(MappingGroup mappingGroup, List<MappingType> mappingTypes);

    List<MappingMetaData> findAllByMappingType(MappingType mappingType);
    Page<MappingMetaData> findAllByAvniValueContains(String avniValue, Pageable pageable);
    Page<MappingMetaData> findAllByBahmniValueContains(String bahmniValue, Pageable pageable);
    Page<MappingMetaData> findAllByAvniValueContainsAndBahmniValueContains(String avniValue, String bahmniValue, Pageable pageable);

    MappingMetaData findByMappingType(MappingType mappingType);

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

    default String getBahmniValue(MappingGroup mappingGroup, MappingType mappingType, String avniValue) {
        MappingMetaData mapping = findByMappingGroupAndMappingTypeAndAvniValue(mappingGroup, mappingType, avniValue);
        if (mapping == null) return null;
        return mapping.getBahmniValue();
    }

    default MappingMetaData getConceptMappingByOpenMRSConcept(String openMRSConceptUuid, BahmniEncounterToAvniEncounterMetaData bahmniEncounterToAvniEncounterMetaData, boolean isIgnorable) {
        MappingMetaData mappingMetaData = findByMappingGroupAndMappingTypeAndBahmniValue(MappingGroup.Observation, MappingType.Concept, openMRSConceptUuid);
        if (mappingMetaData == null && isIgnorable && !bahmniEncounterToAvniEncounterMetaData.isIgnoredInBahmni(openMRSConceptUuid))
            throw new RuntimeException(String.format("No mapping found for openmrs concept with uuid = %s and is also not ignored", openMRSConceptUuid));
        return mappingMetaData;
    }

    default MappingMetaData saveMapping(MappingGroup mappingGroup, MappingType mappingType, String bahmniValue, String avniValue, ObsDataType obsDataType) {
        MappingMetaData mappingMetaData = createMappingMetaData(mappingGroup, mappingType, bahmniValue, avniValue);
        mappingMetaData.setDataTypeHint(obsDataType);
        return save(mappingMetaData);
    }

    private MappingMetaData createMappingMetaData(MappingGroup mappingGroup, MappingType mappingType, String bahmniValue, String avniValue) {
        MappingMetaData mappingMetaData = new MappingMetaData();
        mappingMetaData.setMappingGroup(mappingGroup);
        mappingMetaData.setMappingType(mappingType);
        mappingMetaData.setBahmniValue(bahmniValue);
        mappingMetaData.setAvniValue(avniValue);
        return mappingMetaData;
    }

    default MappingMetaData saveMapping(MappingGroup mappingGroup, MappingType mappingType, String bahmniValue, String avniValue) {
        MappingMetaData mappingMetaData = createMappingMetaData(mappingGroup, mappingType, bahmniValue, avniValue);
        return save(mappingMetaData);
    }

    default String getBahmniValueForAvniIdConcept() {
        return getBahmniValue(MappingGroup.Common, MappingType.AvniUUID_Concept);
    }

    default String getBahmniFormUuidForProgramEncounter(String encounterType) {
        return getBahmniValue(MappingGroup.ProgramEncounter,
                MappingType.CommunityProgramEncounter_BahmniForm,
                encounterType);
    }

    default String getBahmniFormUuidForGeneralEncounter(String encounterType) {
        return getBahmniValue(MappingGroup.GeneralEncounter,
                MappingType.CommunityEncounter_BahmniForm,
                encounterType);
    }
}
