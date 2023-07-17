package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegrationSystemRepository extends BaseRepository<IntegrationSystem> {
    IntegrationSystem findBySystemType(IntegrationSystem.IntegrationSystemType type);
    List<IntegrationSystem> findAllBySystemType(IntegrationSystem.IntegrationSystemType integrationSystemType);
}
