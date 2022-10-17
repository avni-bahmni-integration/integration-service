package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationSystemRepository extends BaseRepository<IntegrationSystem> {
    IntegrationSystem findByName(String name);
}
