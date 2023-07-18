package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.avni_integration_service.integration_data.domain.MappingMetaData;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.avni_integration_service.integration_data.domain.framework.MappingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MappingMetaDataRepository extends PagingAndSortingRepository<MappingMetaData, Integer> {
    MappingMetaData findByIdAndIntegrationSystem(int id, IntegrationSystem integrationSystem);

    MappingMetaData findByMappingGroupAndMappingType(MappingGroup mappingGroup, MappingType mappingType);

    MappingMetaData findByMappingGroupNameAndMappingTypeNameAndIntSystemValueAndIntegrationSystem(String mappingGroup, String mappingType, String intSystemValue, IntegrationSystem integrationSystem);
    MappingMetaData findByMappingGroupNameAndMappingTypeNameAndIntSystemValueAndIntegrationSystemId(String mappingGroup, String mappingType, String intSystemValue, int integrationSystemId);

    MappingMetaData findByMappingGroupNameAndMappingTypeNameAndAvniValueAndIntegrationSystem(String mappingGroup, String mappingType, String avniValue, IntegrationSystem integrationSystem);
    MappingMetaData findByMappingGroupNameAndMappingTypeNameAndAvniValueAndIntegrationSystemId(String mappingGroup, String mappingType, String avniValue, int integrationSystemId);

    default MappingMetaData getAvniMapping(String mappingGroup, String mappingType, String intSystemValue, IntegrationSystem integrationSystem) {
        MappingMetaData mapping = this.getAvniMappingIfPresent(mappingGroup, mappingType, intSystemValue, integrationSystem);
        if (mapping == null)
            throw new MappingException(String.format("No mapping found for MappingGroup: %s, MappingType: %s, IntSystemValue: %s", mappingGroup, mappingType, intSystemValue));
        return mapping;
    }

    default MappingMetaData getAvniMappingIfPresent(String mappingGroup, String mappingType, String intSystemValue, IntegrationSystem integrationSystem) {
        return findByMappingGroupNameAndMappingTypeNameAndIntSystemValueAndIntegrationSystem(mappingGroup, mappingType, intSystemValue, integrationSystem);
    }

    default MappingMetaData getAvniMappingIfPresent(String mappingGroup, String mappingType, String intSystemValue, int integrationSystemId) {
        return findByMappingGroupNameAndMappingTypeNameAndIntSystemValueAndIntegrationSystemId(mappingGroup, mappingType, intSystemValue, integrationSystemId);
    }

    default MappingMetaData getIntSystemMappingIfPresent(String mappingGroup, String mappingType, String avniMapping, IntegrationSystem integrationSystem) {
        return findByMappingGroupNameAndMappingTypeNameAndAvniValueAndIntegrationSystem(mappingGroup, mappingType, avniMapping, integrationSystem);
    }

    default MappingMetaData getIntSystemMappingIfPresent(String mappingGroup, String mappingType, String avniMapping, int integrationSystemId) {
        return findByMappingGroupNameAndMappingTypeNameAndAvniValueAndIntegrationSystemId(mappingGroup, mappingType, avniMapping, integrationSystemId);
    }

    MappingMetaData findByMappingGroupAndMappingTypeAndIntSystemValue(MappingGroup mappingGroup, MappingType mappingType, String intSystemValue);

    MappingMetaData findByMappingGroupAndMappingTypeAndAvniValue(MappingGroup mappingGroup, MappingType mappingType, String avniValue);

    List<MappingMetaData> findAllByMappingGroupAndMappingType(MappingGroup mappingGroup, MappingType mappingType);

    List<MappingMetaData> findAllByMappingGroupAndMappingTypeIn(MappingGroup mappingGroup, List<MappingType> mappingTypes);

    List<MappingMetaData> findAllByMappingGroupNameAndIntegrationSystem(String mappingGroup, IntegrationSystem integrationSystem);

    List<MappingMetaData> findAllByMappingType(MappingType mappingType);

    Page<MappingMetaData> findAllByAvniValueContainsAndIntegrationSystem(String avniValue, IntegrationSystem integrationSystem, Pageable pageable);

    Page<MappingMetaData> findAllByIntSystemValueContainsAndIntegrationSystem(String intSystemValue, IntegrationSystem integrationSystem, Pageable pageable);

    Page<MappingMetaData> findAllByAvniValueContainsAndIntSystemValueContainsAndIntegrationSystem(String avniValue, String intSystemValue, IntegrationSystem integrationSystem, Pageable pageable);

    MappingMetaData findByMappingType(MappingType mappingType);

    List<MappingMetaData> findAllByMappingTypeInAndAvniValue(Collection<MappingType> mappingTypes, String avniValue);

    Page<MappingMetaData> findAllByIntegrationSystem(IntegrationSystem currentIntegrationSystem, Pageable pageable);
}
