package org.bahmni_avni_integration.integration_data.repository;

import org.bahmni_avni_integration.integration_data.domain.IgnoredBahmniConcept;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IgnoredBahmniConceptRepository extends CrudRepository<IgnoredBahmniConcept, Integer> {
}
