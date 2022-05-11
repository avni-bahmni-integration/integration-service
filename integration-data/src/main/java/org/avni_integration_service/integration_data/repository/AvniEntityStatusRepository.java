package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.AvniEntityStatus;
import org.avni_integration_service.integration_data.domain.AvniEntityType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvniEntityStatusRepository extends CrudRepository<AvniEntityStatus, Integer> {
    AvniEntityStatus findByEntityType(AvniEntityType avniEntityType);
}
