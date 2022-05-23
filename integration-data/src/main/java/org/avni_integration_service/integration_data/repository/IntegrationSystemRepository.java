package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationSystemRepository extends PagingAndSortingRepository<IntegrationSystem, Integer> {
    IntegrationSystem findByName(String name);
}
