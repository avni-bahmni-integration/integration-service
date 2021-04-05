package org.bahmni_avni_integration.integration_data.repository;

import org.bahmni_avni_integration.entity.FailedEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedEventRepository extends CrudRepository<FailedEvent, Integer>  {
}