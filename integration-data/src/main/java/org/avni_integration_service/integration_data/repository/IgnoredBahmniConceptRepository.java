package org.avni_integration_service.integration_data.repository;

import org.avni_integration_service.integration_data.domain.IgnoredBahmniConcept;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IgnoredBahmniConceptRepository extends CrudRepository<IgnoredBahmniConcept, Integer> {
    IgnoredBahmniConcept findByConceptUuid(String conceptUuid);
}
