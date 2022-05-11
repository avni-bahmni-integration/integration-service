package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.entity.FailedEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FailedEventRepository extends CrudRepository<FailedEvent, Integer>  {
}
