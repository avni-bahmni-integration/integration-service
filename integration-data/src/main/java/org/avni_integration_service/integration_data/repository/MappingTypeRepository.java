package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.MappingType;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MappingTypeRepository extends PagingAndSortingRepository<MappingType, Integer> {
    MappingType findByName(String name);
    List<MappingType> findByIdIn(Integer[] ids);
    List<MappingType> findAllByIntegrationSystem(IntegrationSystem integrationSystem);
}
