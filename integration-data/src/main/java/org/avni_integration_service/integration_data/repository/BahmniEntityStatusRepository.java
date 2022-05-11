package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.BahmniEntityType;
import org.avni_integration_service.integration_data.domain.BahmniEntityStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BahmniEntityStatusRepository extends CrudRepository<BahmniEntityStatus, Integer> {
    BahmniEntityStatus findByEntityType(BahmniEntityType avniEntityType);
}
