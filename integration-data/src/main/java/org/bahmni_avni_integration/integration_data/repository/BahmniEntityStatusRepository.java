package org.bahmni_avni_integration.integration_data.repository;

import org.bahmni_avni_integration.integration_data.BahmniEntityType;
import org.bahmni_avni_integration.integration_data.domain.BahmniEntityStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BahmniEntityStatusRepository extends CrudRepository<BahmniEntityStatus, Integer> {
    BahmniEntityStatus findByEntityType(BahmniEntityType avniEntityType);
}