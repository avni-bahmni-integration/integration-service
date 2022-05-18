package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MappingMetaDataRepository extends PagingAndSortingRepository<MappingMetaData, Integer> {
    MappingMetaData findByMappingGroupAndMappingType(MappingGroup mappingGroup, MappingType mappingType);

    MappingMetaData findByMappingGroupAndMappingTypeAndIntSystemValue(MappingGroup mappingGroup, MappingType mappingType, String bahmniValue);

    MappingMetaData findByMappingGroupAndMappingTypeAndAvniValue(MappingGroup mappingGroup, MappingType mappingType, String avniValue);

    List<MappingMetaData> findAllByMappingGroupAndMappingType(MappingGroup mappingGroup, MappingType mappingType);

    List<MappingMetaData> findAllByMappingGroupAndMappingTypeIn(MappingGroup mappingGroup, List<MappingType> mappingTypes);

    List<MappingMetaData> findAllByMappingType(MappingType mappingType);
    Page<MappingMetaData> findAllByAvniValueContains(String avniValue, Pageable pageable);
    Page<MappingMetaData> findAllByIntSystemValueContains(String bahmniValue, Pageable pageable);
    Page<MappingMetaData> findAllByAvniValueContainsAndIntSystemValueContains(String avniValue, String bahmniValue, Pageable pageable);

    MappingMetaData findByMappingType(MappingType mappingType);

    List<MappingMetaData> findAllByMappingTypeInAndAvniValue(Collection<MappingType> mappingTypes, String avniValue);
}
