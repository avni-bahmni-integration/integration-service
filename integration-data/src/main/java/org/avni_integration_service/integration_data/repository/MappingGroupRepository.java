package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.avni_integration_service.integration_data.domain.MappingGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MappingGroupRepository extends BaseRepository<MappingGroup> {
    MappingGroup findByName(String name);
    List<MappingGroup> findAllByIntegrationSystem(IntegrationSystem integrationSystem);
}
