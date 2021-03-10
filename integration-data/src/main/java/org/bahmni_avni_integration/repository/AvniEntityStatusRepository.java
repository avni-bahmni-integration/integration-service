package org.bahmni_avni_integration.repository;

import org.bahmni_avni_integration.domain.AvniEntityStatus;
import org.bahmni_avni_integration.domain.AvniEntityType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvniEntityStatusRepository extends CrudRepository<AvniEntityStatus, Integer> {
    AvniEntityStatus findByEntityType(AvniEntityType avniEntityType);
}