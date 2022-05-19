package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationSystemRepository extends CrudRepository<IntegrationSystem, Integer> {
    IntegrationSystem findByName(String name);
}
