package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.IntegratingEntityStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegratingEntityStatusRepository extends CrudRepository<IntegratingEntityStatus, Integer> {
    IntegratingEntityStatus findByEntityType(String entityType);
}
