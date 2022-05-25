package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.IntegrationSystem;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntegrationSystemRepository extends PagingAndSortingRepository<IntegrationSystem, Integer> {
    IntegrationSystem findByName(String name);
    List<IntegrationSystem> findByIdIn(Integer[] ids);
}
